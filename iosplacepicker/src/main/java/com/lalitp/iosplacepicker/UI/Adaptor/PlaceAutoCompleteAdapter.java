package com.lalitp.iosplacepicker.UI.Adaptor;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.lalitp.iosplacepicker.Pojo.PlaceSearch.Prediction;
import com.lalitp.iosplacepicker.R;
import com.lalitp.iosplacepicker.R2;

import java.text.Normalizer;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by atulsia-fifteen on 10/1/18.
 */

public class PlaceAutoCompleteAdapter extends RecyclerView.Adapter<PlaceAutoCompleteAdapter.ViewHolder> {


    private Context context;
    private List<Prediction> itemList;
    private ItemClickListner mclickListner;
    private String searchQuery = "";

    public PlaceAutoCompleteAdapter(Context context, List<Prediction> itemList) {

        this.context = context;
        this.itemList = itemList;
    }

    public void setOnItemClickListener(ItemClickListner myClickListener) {
        this.mclickListner = myClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.autocomplete_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Prediction placeAutoComplete = itemList.get(position);
        String title = placeAutoComplete.getStructuredFormatting().getMainText();
        String description = placeAutoComplete.getStructuredFormatting().getSecondaryText();

        holder.placeName.setText(highlightText(searchQuery,title));
        holder.placeDetail.setText(description);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface ItemClickListner {
        void onItemclicked(int pos);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.place_name)
        TextView placeName;
        @BindView(R2.id.place_detail)
        TextView placeDetail;

        public ViewHolder(View itemview) {
            super(itemview);
            ButterKnife.bind(this, itemview);
            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mclickListner != null) {
                        mclickListner.onItemclicked(getAdapterPosition());
                    }
                }
            });
        }
    }

    public static CharSequence highlightText(String search, String originalText) {
        if (search != null && !search.equalsIgnoreCase("")) {
            String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
            int start = normalizedText.indexOf(search);
            if (start < 0) {
                return originalText;
            } else {
                Spannable highlighted = new SpannableString(originalText);
                while (start >= 0) {
                    int spanStart = Math.min(start, originalText.length());
                    int spanEnd = Math.min(start + search.length(), originalText.length());
                    highlighted.setSpan(new ForegroundColorSpan(Color.WHITE), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = normalizedText.indexOf(search, spanEnd);
                }
                return highlighted;
            }
        }
        return originalText;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}
