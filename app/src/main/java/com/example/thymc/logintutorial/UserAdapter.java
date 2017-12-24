package com.example.thymc.logintutorial;

   import java.util.ArrayList;
        import java.util.List;

        import android.content.Context;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.BaseAdapter;
   import android.widget.Button;
   import android.widget.Filter;
        import android.widget.Filterable;
        import android.widget.TextView;

   import com.android.volley.RequestQueue;
   import com.android.volley.Response;
   import com.android.volley.toolbox.Volley;


public class UserAdapter extends ArrayAdapter<User> implements Filterable {

    private List<User> planetList;
    private Context context;
    private Filter planetFilter;
    private List<User> origPlanetList;
    private String username;

    public UserAdapter(List<User> planetList, Context ctx,String username) {
        super(ctx, R.layout.userlist_item, planetList);
        this.planetList = planetList;
        this.context = ctx;
        this.origPlanetList = planetList;
        this.username = username;
    }

    public int getCount() {
        return planetList.size();
    }

    public User getItem(int position) {
        return planetList.get(position);
    }

    public long getItemId(int position) {
        return planetList.get(position).hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        PlanetHolder holder = new PlanetHolder();

        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.userlist_item, null);
            // Now we can fill the layout with the right values
            TextView tv = (TextView) v.findViewById(R.id.userlist_item_text);


            holder.planetNameView = tv;

            v.setTag(holder);
        }
        else
            holder = (PlanetHolder) v.getTag();

        final User p = planetList.get(position);
        holder.planetNameView.setText(p.getName());
        Button list_But=(Button)v.findViewById(R.id.userlist_item_button_accept);
        list_But.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        planetList.remove(planetList.get(position));
                        notifyDataSetChanged();
                    }
                };
                List<String> argList = new ArrayList<>();
                argList.add("denemeAndroid");
                argList.add( p.getName());
                argList.add(username);
                RequestServer request = new RequestServer("friendRequest",argList,responseListener);
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });

        return v;
    }

    public void resetData() {
        planetList = origPlanetList;
    }


	/* *********************************
	 * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/

    private static class PlanetHolder {
        public TextView planetNameView;
    }



	/*
	 * We create our filter
	 */

    @Override
    public Filter getFilter() {
        if (planetFilter == null)
            planetFilter = new PlanetFilter();

        return planetFilter;
    }



    private class PlanetFilter extends Filter {



        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = origPlanetList;
                results.count = origPlanetList.size();
            }
            else {
                // We perform filtering operation
                List<User> nPlanetList = new ArrayList<User>();

                for (User p : planetList) {
                    if (p.getName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nPlanetList.add(p);
                }

                results.values = nPlanetList;
                results.count = nPlanetList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                planetList = (List<User>) results.values;
                notifyDataSetChanged();
            }

        }

    }
}
