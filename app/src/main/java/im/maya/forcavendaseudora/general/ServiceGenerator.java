package im.maya.forcavendaseudora.general;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by ricardosousa on 8/27/15.
 */
public class ServiceGenerator {

    private ServiceGenerator() {

    }

    public static <S> S createService(Class<S> serviceClass) {

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint("http://fv.eudoracloud.com/service/api/bulletin")
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);

    }
}
