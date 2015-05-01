package com.unionbigdata.takepicbuy.baseclass;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.unionbigdata.takepicbuy.http.OnAdapterLoadMoreOverListener;
import com.unionbigdata.takepicbuy.http.OnAdapterRefreshOverListener;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class SuperAdapter extends BaseAdapter {
	protected static final int REFRESH = 1;
	protected static final int LOADMORE = 2;
	public static final String ISOVER = "已是全部";
	public static final String ISNULL = "NO_CONTENT";
	public static final String ISERROR = "ERROR_DATA";
	public static final String CLICK_MORE = "点击更多";
	public static final String CACHE = "没有了";
	protected int loadType;
	private OnAdapterRefreshOverListener refreshOverListener;
	private OnAdapterLoadMoreOverListener loadMoreOverListener;
	protected boolean isRequest;
	private List msgList;
	protected Context context;
	protected LayoutInflater mInflater;

	public SuperAdapter(Context context) {
		this.context = context;
		this.msgList = new ArrayList<Object>();
		this.isRequest = false;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public boolean isRequest() {
		return isRequest;
	}
	
	public ArrayList<Object> getList(){
		return (ArrayList<Object>) msgList;
	}

	public void setRefreshOverListener(OnAdapterRefreshOverListener listener) {
		this.refreshOverListener = listener;
	}

	public void refreshOver(int code, String msg) {
		if (refreshOverListener != null) {
			refreshOverListener.refreshOver(code, msg);
		}
	}

	public void setLoadMoreOverListener(OnAdapterLoadMoreOverListener listener) {
		this.loadMoreOverListener = listener;
	}

	public void loadMoreOver(int code, String msg) {
		if (loadMoreOverListener != null) {
			loadMoreOverListener.loadMoreOver(code, msg);
		}
	}

	public List getMsgList() {
		return msgList;
	}

	public Context getContext() {
		return this.context;
	}

	public int getCount() {
		return this.msgList.size();
	}

	public Object getItem(int position) {
		if (position >= 0 && position < getCount()) {
			return this.msgList.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void clearList() {
		msgList.clear();
		notifyDataSetChanged();
	}

	public void refreshItems(List items) {
		msgList.clear();
		if (items != null) {
			msgList.addAll(items);
		}
		notifyDataSetChanged();
	}

	public void addItems(List items) {
		if (items != null) {
			msgList.addAll(getCount(), items);
		}
		notifyDataSetChanged();
	}
	
	public void addItemsNoChanged(List items) {
		if (items != null) {
			msgList.addAll(getCount(), items);
		}
//		notifyDataSetChanged();
	}

	public void addItemsInFront(List items) {
		if (items != null) {
			msgList.addAll(0, items);
		}
		notifyDataSetChanged();
	}

	public void addItem(Object item) {
		if (item != null) {
			msgList.add(item);
		}
		notifyDataSetChanged();
	}

	public void addItem(Object item, int position) {
		if (item != null) {
			msgList.add(position, item);
		}
		notifyDataSetChanged();
	}

	public boolean removeItem(int position) {
		if (position >= 0 && position < getCount()) {
			msgList.remove(position);
			notifyDataSetChanged();
			return true;
		}
		return false;
	}
	
	public boolean remove(int position) {
		if (position >= 0 && position < getCount()) {
			msgList.remove(position);
			return true;
		}
		return false;
	}
	
	public void toast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public abstract void refresh();

	public abstract void loadMore();
}
