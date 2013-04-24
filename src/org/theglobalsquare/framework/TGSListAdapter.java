package org.theglobalsquare.framework;

import java.util.Collection;

import org.theglobalsquare.app.R;
import org.theglobalsquare.framework.values.TGSCommunity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public abstract class TGSListAdapter extends ArrayAdapter<ITGSObject> {
	public TGSListAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	@Override
	public View getView(int position, View convertView,
			ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		TextView name = (TextView)row.findViewById(R.id.communityName);
		ITGSObject o = getItem(position);
		if(name != null) {
			if(o instanceof TGSCommunity) {
				TGSCommunity c = (TGSCommunity)o;
				name.setText(c.getName());
				TextView description = (TextView)row.findViewById(R.id.communityDescription);
				description.setText(c.getDescription());
				
				// TODO set image for avatar
				
			} else {
				name.setText(R.string.errUnrecognizedType);
			}
		}
		return row;
	}
	
	@Override
	public int getCount() {
		return getItems().size();
	}

	@Override
	public ITGSObject getItem(int position) {
		return getItems().get(position);
	}

	@Override
	public void add(ITGSObject object) {
		getItems().add(object);
	}

	@Override
	public void addAll(Collection<? extends ITGSObject> collection) {
		getItems().addAll(collection);
	}

	@Override
	public void clear() {
		getItems().clear();
	}

	@Override
	public void insert(ITGSObject object, int index) {
		getItems().add(index, object);
	}

	@Override
	public void remove(ITGSObject object) {
		getItems().remove(object);
	}

	public abstract TGSObjectList getItems();
}
