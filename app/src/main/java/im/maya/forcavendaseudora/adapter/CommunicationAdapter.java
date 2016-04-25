package im.maya.forcavendaseudora.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import im.maya.forcavendaseudora.R;
import im.maya.forcavendaseudora.response.Communication;

/**
 * Created by ricardo.sousa on 1/30/15.
 */
public class CommunicationAdapter extends ArrayAdapter<Communication> {

    public List<Communication> messageItem;
    private Context context;
    private int layoutId;


    public CommunicationAdapter(Context context, List<Communication> messageItem, int layoutId) {
        super(context, layoutId, messageItem);

        this.context = context;

        if (messageItem == null)
            messageItem = new ArrayList<>();

        this.messageItem = messageItem;
        this.layoutId = layoutId;
    }


    public static class ViewHolder{

        private TextView date;
        private TextView title;

    }

    @Override
    public int getCount() {
        return messageItem.size();
    }

    @Override
    public Communication getItem(int index) {

        if (messageItem.isEmpty())
            return null;

        return messageItem.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Communication commu = messageItem.get(position);
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, parent, false);

            holder = new ViewHolder();

            holder.date = (TextView) convertView.findViewById(R.id.feed_text_date);
            holder.title = (TextView) convertView.findViewById(R.id.feed_text_title);

            Typeface fontListTitle = Typeface.createFromAsset(convertView.getContext().getAssets(), "fonts/radikal.otf");
            holder.date.setTypeface(fontListTitle);
            holder.title.setTypeface(fontListTitle);

            if(commu.isReaded()) {

                holder.date.setText(Html.fromHtml(commu.getDate()));
                holder.date.setTextAppearance(context, R.style.MessageDateReaded);

                holder.title.setText(Html.fromHtml(commu.getTitle()));
                holder.title.setTextAppearance(context, R.style.MessageTitleReaded);

            }


            convertView.setTag(holder);

        }
        else{
            View layout = convertView;

            holder = (ViewHolder) layout.getTag();
        }


        holder.date.setText(Html.fromHtml(commu.getDate()));
        holder.title.setText(Html.fromHtml(commu.getTitle()));

        return convertView;
    }

}