package com.example.ja.diudiu;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ja.diudiu.adapter.BaseAdapterHelper;
import com.example.ja.diudiu.adapter.QuickAdapter;
import com.example.ja.diudiu.base.EditPopupWindow;
import com.example.ja.diudiu.bean.Found;
import com.example.ja.diudiu.bean.Lost;
import com.example.ja.diudiu.config.Constants;
import com.example.ja.diudiu.i.IPopupItemClick;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;


public class MainActivity extends BaseActivity implements View.OnClickListener, IPopupItemClick, AdapterView.OnItemLongClickListener {
    RelativeLayout layoutAction;
    LinearLayout layoutAll;
    TextView tvLost;
    ListView listView;
    Button btnAdd;
    protected QuickAdapter<Lost> LostAdapter;
    protected QuickAdapter<Found> FoundAdapter;
    private Button layoutFound;
    private Button layoutLost;
    PopupWindow morePop;
    RelativeLayout progress;
    LinearLayout layoutNo;
    TextView tvNo;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }

    public void initViews() {
        progress = (RelativeLayout) findViewById(R.id.progress);
        layoutNo = (LinearLayout) findViewById(R.id.layout_no);
        tvNo = (TextView) findViewById(R.id.tv_no);
        layoutAction = (RelativeLayout) findViewById(R.id.layout_action_2);
        layoutAll = (LinearLayout) findViewById(R.id.layout_all);
        tvLost = (TextView) findViewById(R.id.tv_lost);
        tvLost.setTag("Lost");
        listView = (ListView) findViewById(R.id.list_lost);
        btnAdd = (Button) findViewById(R.id.btn_add);
        initEditPop();
    }

    public void initListeners() {
        listView.setOnItemLongClickListener(this);
        btnAdd.setOnClickListener(this);
        layoutAll.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == layoutAll) {
            showListPop();
        } else if (v == btnAdd) {
            Intent intent = new Intent(this, AddActivity.class);
            intent.putExtra("from", tvLost.getTag().toString());
            startActivityForResult(intent, Constants.REQUESTCODE_ADD);
        } else if (v == layoutFound) {
            changeTextView(v);
            morePop.dismiss();
            queryFounds();
        } else if (v == layoutLost) {
            changeTextView(v);
            morePop.dismiss();
            queryLosts();
        }
    }

    public void initData() {
        if (LostAdapter == null) {
            LostAdapter = new QuickAdapter<Lost>(this, R.layout.item_list) {
                @Override
                protected void convert(BaseAdapterHelper helper, Lost lost) {
                    helper.setText(R.id.tv_title, lost.getTitle())
                            .setText(R.id.tv_describe, lost.getDescribe())
                            .setText(R.id.tv_time, lost.getCreatedAt())
                            .setText(R.id.tv_photo, lost.getPhone());
                }
            };
        }
        if (FoundAdapter == null) {
            FoundAdapter = new QuickAdapter<Found>(this, R.layout.item_list) {
                @Override
                protected void convert(BaseAdapterHelper helper, Found found) {
                    helper.setText(R.id.tv_title, found.getTitle())
                            .setText(R.id.tv_describe, found.getDescribe())
                            .setText(R.id.tv_time, found.getCreatedAt())
                            .setText(R.id.tv_photo, found.getPhone());
                }
            };
        }
        listView.setAdapter(LostAdapter);
        queryLosts();
    }

    private void changeTextView(View v) {
        if (v == layoutFound) {
            tvLost.setTag("Found");
            tvLost.setText("Found");
        } else {
            tvLost.setTag("Lost");
            tvLost.setText("Lost");
        }
    }

    private void showListPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_lost, null);//注入

        layoutFound = (Button) view.findViewById(R.id.layout_found);
        layoutLost = (Button) view.findViewById(R.id.layout_lost);
        layoutFound.setOnClickListener(MainActivity.this);
        layoutLost.setOnClickListener(MainActivity.this);
        morePop = new PopupWindow(view, mScreenWidth, 600);
        morePop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    morePop.dismiss();
                    return true;
                }
                return false;
            }
        });
        morePop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        morePop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        morePop.setTouchable(true);
        morePop.setFocusable(true);
        morePop.setOutsideTouchable(true);
        morePop.setBackgroundDrawable(getResources().getDrawable(R.drawable.base_pop_bg_n));
        morePop.setAnimationStyle(R.style.MenuPop);
        morePop.showAsDropDown(layoutAction, 0, -1 * dip2px(this, 2.0F));
    }

    private void initEditPop() {
        mPopupWindow = new EditPopupWindow(this, 200, 48);
        mPopupWindow.setmOnPopItemClickListener(this);
    }

    EditPopupWindow mPopupWindow;
    int position;


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP,
                location[0], getStateBar() + location[1]);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUESTCODE_ADD:
                String tag = tvLost.getTag().toString();
                if (tag.equals("Lost")) {
                    queryLosts();
                } else {
                    queryFounds();
                }
                break;
        }
    }

    private void queryLosts() {
        showView();
        BmobQuery<Lost> query = new BmobQuery<Lost>();
        query.order("-createdAt");
        query.findObjects(this, new FindListener<Lost>() {
            @Override
            public void onSuccess(List<Lost> list) {
                LostAdapter.clear();
                FoundAdapter.clear();
                if (list == null || list.size() == 0) {
                    showErrorView(0);
                    LostAdapter.notifyDataSetChanged();
                    return;
                }
                progress.setVisibility(View.GONE);
                LostAdapter.addAll(list);
                listView.setAdapter(LostAdapter);
            }

            @Override
            public void onError(int i, String s) {
                showErrorView(0);
            }
        });
    }

    /*
    查询招领结果
     */
    private void queryFounds() {
        showView();
        BmobQuery<Found> query = new BmobQuery<Found>();
        query.order("-createdAt");
        query.findObjects(this, new FindListener<Found>() {
            @Override
            public void onSuccess(List<Found> list) {
                FoundAdapter.clear();
                FoundAdapter.clear();
                if (list == null || list.size() == 0) {
                    showErrorView(0);
                    FoundAdapter.notifyDataSetChanged();
                    return;
                }
                progress.setVisibility(View.GONE);
                FoundAdapter.addAll(list);
                listView.setAdapter(FoundAdapter);
            }

            @Override
            public void onError(int i, String s) {
                showErrorView(1);
            }
        });
    }

    /*
    *请求出错或者无数据时候显示的界面
     */
    private void showErrorView(int tag) {
        progress.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        layoutNo.setVisibility(View.VISIBLE);
        if (tag == 0) {
            tvNo.setText("暂时还未有失物信息");
        } else {
            tvNo.setText("暂时还没有人来领");
        }
    }

    private void showView() {
        listView.setVisibility(View.VISIBLE);
        layoutNo.setVisibility(View.GONE);
    }

    @Override
    public void onEdit(View v) {
        String tag = tvLost.getTag().toString();
        Intent intent = new Intent(this, AddActivity.class);
        String title = "";
        String describe = "";
        String phone = "";
        if (tag.equals("Lost")) {
            title = LostAdapter.getItem(position).getTitle();
            describe = LostAdapter.getItem(position).getDescribe();
            phone = LostAdapter.getItem(position).getPhone();
        } else {
            title = FoundAdapter.getItem(position).getTitle();
            describe = FoundAdapter.getItem(position).getDescribe();
            phone = FoundAdapter.getItem(position).getPhone();
        }
        intent.putExtra("describe", describe);
        intent.putExtra("phone", phone);
        intent.putExtra("title", title);
        intent.putExtra("from", tag);
        startActivityForResult(intent, Constants.REQUESTCODE_ADD);
    }

    @Override
    public void onDelete(View v) {
        String tag = tvLost.getTag().toString();
        if (tag.equals("Lost")) {
            deleteLost();
        } else {
            deleteFound();
        }

    }

    private void deleteLost() {
        Lost lost = new Lost();
        lost.setObjectId(LostAdapter.getItem(position).getObjectId());
        lost.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                LostAdapter.remove(position);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }


    private void deleteFound() {
        Found found = new Found();
        found.setObjectId(FoundAdapter.getItem(position).getObjectId());
        found.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                FoundAdapter.remove(position);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }
}
