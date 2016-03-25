package com.github.mzule.androidweekly.ui.activity;

import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.mzule.androidweekly.R;
import com.github.mzule.androidweekly.api.ApiCallback;
import com.github.mzule.androidweekly.api.ArticleApi;
import com.github.mzule.androidweekly.entity.Article;
import com.github.mzule.androidweekly.entity.Issue;
import com.github.mzule.androidweekly.ui.adapter.ArticleAdapter;
import com.github.mzule.androidweekly.ui.adapter.SlideAdapter;
import com.github.mzule.androidweekly.ui.view.ProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnItemClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.progressView)
    ProgressView progressView;
    @Bind(R.id.slideListView)
    ListView slideListView;
    private ArticleAdapter adapter;
    private SlideAdapter slideAdapter;
    private List<Issue> issues;

    @OnItemClick(R.id.slideListView)
    void onSlideItemClick(AdapterView<?> parent, View view, int position, long id) {
        Issue issue = (Issue) parent.getAdapter().getItem(position);
        active(issue);
        slideAdapter.notifyDataSetChanged();

        sendRequest(issue.getName());

        drawerLayout.closeDrawers();
        progressView.start();
    }

    @OnItemClick(R.id.listView)
    void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Article article = (Article) parent.getAdapter().getItem(position);
        startActivity(ArticleActivity.makeIntent(this, article));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void afterInject() {
        adapter = new ArticleAdapter(this);
        listView.setAdapter(adapter);

        slideAdapter = new SlideAdapter(this);
        slideListView.setAdapter(slideAdapter);

        sendRequest(null);
    }


    private void active(Issue issue) {
        for (Issue i : issues) {
            i.setActive(i == issue);
        }
    }

    private void sendRequest(String issue) {
        new ArticleApi().getPage(issue, new ApiCallback<List<Object>>() {
            @Override
            public void onSuccess(List<Object> data) {
                adapter.clear();
                adapter.addAndNotify(data);
                listView.setSelection(0);
                progressView.finish();

                if (issues == null) {
                    issues = new ArrayList<>();
                    for (int i = 197; i > 0; i--) {
                        issues.add(new Issue("issue-" + i, i == 197));
                    }
                    slideAdapter.clear();
                    slideAdapter.addAndNotify(issues);
                }
            }

            @Override
            public void onFailure(Exception e) {
                progressView.finish();
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }
}
