package com.google.firebase.codelab.friendlychat.chatAddons.movie.helpers;

import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.sothree.slidinguppanel.ScrollableViewHelper;

/**
 * Created by Disha on 12/1/16.
 */

public class NestedScrollableViewHelper extends ScrollableViewHelper {
    private View mgridView;
    public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
        if (mgridView instanceof NestedScrollView) {
            if(isSlidingUp){
                return mgridView.getScrollY();
            } else {
                NestedScrollView nsv = ((NestedScrollView) mgridView);
                View child = nsv.getChildAt(0);
                return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
            }
        } else {
            return 0;
        }
    }
    public void setScrollableView(View scrollableView) {
        	        mgridView = scrollableView;
            }
}