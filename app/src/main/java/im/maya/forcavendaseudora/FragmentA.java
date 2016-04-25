package im.maya.forcavendaseudora;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import im.maya.forcavendaseudora.adapter.CommunicationAdapter;
import im.maya.forcavendaseudora.general.Repository;
import im.maya.forcavendaseudora.general.ServiceGenerator;
import im.maya.forcavendaseudora.request.BaseRequest;
import im.maya.forcavendaseudora.response.Communication;
import im.maya.forcavendaseudora.response.ReturnService;
import im.maya.forcavendaseudora.webservice.WebserviceEudora;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentA extends android.support.v4.app.Fragment implements AbsListView.OnScrollListener{

    public View rootView;

    private ListView communicationListView;
    private CommunicationAdapter adapter = null;
    private ProgressBar feedListProgressBar;

    private String token;
    private int page = 1;
    private int amount = 20;


    private TextView messageNoList;
//    private SwipeRefreshLayout swipeRefreshLayout;


    public boolean validToken;


    public static GoogleAnalytics analytics;
    public static Tracker tracker;


    public FragmentA() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        //===== Analytics Tracker ===================
        analytics = GoogleAnalytics.getInstance(getActivity());
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-64947111-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        //===========================================

        //Sharepreferences
        SharedPreferences prefsList = getActivity().getSharedPreferences("im.maya.forcavendaseudora", Context.MODE_PRIVATE);
        token = prefsList.getString("im.maya.forcavendaseudora.user.Token", "");


        loadComponents();
        loadActions();


        return rootView;
    }


    public void loadComponents(){

        messageNoList = (TextView) rootView.findViewById(R.id.feed_text_nolist);
        feedListProgressBar = (ProgressBar) rootView.findViewById(R.id.feed_list_progress);

//        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.feed_swipe_refresh_layout);
//        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#b22664"), Color.parseColor("#00ff00"), Color.parseColor("#d4c8cc"), Color.parseColor("#8c0a3e"));

        communicationListView = (ListView) rootView.findViewById(R.id.feed_list_comunications);

    }


    public void loadActions(){

        getListData();

        communicationListView.setOnScrollListener(this);

        communicationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Communication communicationSelected = (Communication) parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), FeedDetail.class);
                intent.putExtra("id", String.valueOf(communicationSelected.getId()));
                intent.putExtra("link", communicationSelected.getLink());
                startActivity(intent);

            }
        });


//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//            }
//        });

    }

    public void getListData(){

        feedListProgressBar.setVisibility(View.VISIBLE);
        communicationListView.setEnabled(false);

        final Repository repository = ServiceGenerator.createService(Repository.class);

    repository.getLista(new BaseRequest(token, page, amount), new Callback<ReturnService>() {

        @Override
        public void success(ReturnService returnService, Response response) {

            feedListProgressBar.setVisibility(View.INVISIBLE);
            communicationListView.setEnabled(true);


            if (adapter == null) {
                adapter = new CommunicationAdapter(getActivity(), returnService.getObjectReturn().getList(), R.layout.widget_feed);
                communicationListView.setAdapter(adapter);
            } else {

                adapter.addAll(returnService.getObjectReturn().getList());
                adapter.notifyDataSetChanged();
            }

            if (adapter.isEmpty()) {
                messageNoList.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void failure(RetrofitError error) {

            feedListProgressBar.setVisibility(View.INVISIBLE);
            communicationListView.setEnabled(true);
            Toast.makeText(getActivity(),
                    "Houve um problema na conexão com o servidor. Por favor, encerre o aplicativo e tente novamente mais tarde",
                    Toast.LENGTH_LONG).show();
        }
    });

}


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        int threshold = 1;
        int count = communicationListView.getCount();

        if (scrollState == SCROLL_STATE_IDLE) {
            if (communicationListView.getLastVisiblePosition() >= count - threshold) {

                page = page + 1;
                getListData();
            }
            else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}



    @Override
    public void onResume() {
        super.onResume();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
        Tracker tracker = analytics.newTracker("UA-64947111-1");

        // All subsequent hits will be send with screen name = "main screen"
        tracker.setScreenName("Feed de Comunicados");

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("FragmentFeed")
                .setLabel("Enviar")
                .build());

        validateToken();
    }


    public void validateToken(){

        //Create Request params
        RequestParams params = new RequestParams();
        params.put("Token", token);

        Log.i("Param Set - Token FA", String.valueOf(token));

        //Post ValidToken
        WebserviceEudora config = new WebserviceEudora();

        config.postValidateToken(params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    //if retorno diferente de nulo
                    if (response != null) {

                        validToken = response.getBoolean("Object");
                        Log.i("Response Set - Obj FA", String.valueOf(validToken));

                        if (!validToken) {

                            //Mandar para a tela de Login.
                            Intent intent = new Intent(getActivity(), Login.class);
                            startActivity(intent);

                            getActivity().getSharedPreferences("im.maya.forcavendaseudora.user", 0).edit().clear().commit();
                        }
                    }

                } catch (Exception ex) {
                    Log.e("Parametros FA", ex.getMessage());

                } finally {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        });
    }



}