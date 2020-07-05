package com.tugasakhirmobile.cutesticker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tugasakhirmobile.cutesticker.model.Sticker_packs;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class StickerPackListAdapter extends RecyclerView.Adapter<StickerPackListItemViewHolder> implements Filterable {
    @NonNull
    private List<Sticker_packs> stickerPacks;
    @NonNull
    private List<Sticker_packs> stickerPacksFiltred;
    @NonNull
    private final OnAddButtonClickedListener onAddButtonClickedListener;
    @NonNull
    private final OnContainerLayoutClickedListener OnContainerLayoutClicked;
    private int maxNumberOfStickersInARow;

    StickerPackListAdapter(@NonNull List<Sticker_packs> stickerPacks, @NonNull OnAddButtonClickedListener onAddButtonClickedListener, @NonNull OnContainerLayoutClickedListener OnContainerLayoutClicked, Context mcontext) {
        this.stickerPacks = stickerPacks;
        this.stickerPacksFiltred = stickerPacks;
        this.onAddButtonClickedListener = onAddButtonClickedListener;
        this.OnContainerLayoutClicked = OnContainerLayoutClicked;
    }

    @NonNull
    @Override
    public StickerPackListItemViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final Context context = viewGroup.getContext();
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View stickerPackRow = layoutInflater.inflate(R.layout.sticker_packs_list_item, viewGroup, false);
        return new StickerPackListItemViewHolder(stickerPackRow);
    }

    @Override
    public void onBindViewHolder(@NonNull final StickerPackListItemViewHolder viewHolder, final int index) {
        try {
            Sticker_packs pack = stickerPacksFiltred.get(index);
            final Context context = viewHolder.publisherView.getContext();
            viewHolder.publisherView.setText(pack.publisher);
            // viewHolder.filesizeView.setText(Formatter.formatShortFileSize(context, pack.getTotalSize()));

            viewHolder.titleView.setText(pack.name);
            viewHolder.container.setOnClickListener(v -> OnContainerLayoutClicked.onContainerLayoutClicked(pack));
            viewHolder.imageRowView.removeAllViews();

            //if this sticker pack contains less stickers than the max, then take the smaller size.
            viewHolder.bigcateicon.setImageURI(StickerPackLoader.getStickerAssetUri(pack.identifier, pack.getStickers().get(0).imageFileName));
            int actualNumberOfStickersToShow = Math.min(maxNumberOfStickersInARow, pack.getStickers().size());
            for (int i = 1; i <= actualNumberOfStickersToShow; i++) {
                final SimpleDraweeView rowImage = (SimpleDraweeView) LayoutInflater.from(context).inflate(R.layout.sticker_pack_list_item_image, viewHolder.imageRowView, false);
                rowImage.setImageURI(StickerPackLoader.getStickerAssetUri(pack.identifier, pack.getStickers().get(i).imageFileName));
                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) rowImage.getLayoutParams();
                final int marginBetweenImages = (viewHolder.imageRowView.getMeasuredWidth() - maxNumberOfStickersInARow * viewHolder.imageRowView.getContext().getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size)) / (maxNumberOfStickersInARow - 1) - lp.leftMargin - lp.rightMargin;
                if (i != actualNumberOfStickersToShow - 1 && marginBetweenImages > 0) { //do not set the margin for the last image
                    lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin + marginBetweenImages, lp.bottomMargin);
                    rowImage.setLayoutParams(lp);
                }
                viewHolder.imageRowView.addView(rowImage);
            }
            setAddButtonAppearance(viewHolder.addButton, pack);
        } catch (Exception e) {
        }
    }

    private void setAddButtonAppearance(ImageView addButton, Sticker_packs pack) {
        if (pack.getIsWhitelisted()) {
            addButton.setImageResource(R.drawable.ic_check);
            addButton.setClickable(false);
            addButton.setOnClickListener(null);
            setBackground(addButton, null);
        } else {
            addButton.setImageResource(R.drawable.ic_add);
            addButton.setOnClickListener(v -> onAddButtonClickedListener.onAddButtonClicked(pack));
            TypedValue outValue = new TypedValue();
            addButton.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            addButton.setBackgroundResource(outValue.resourceId);
        }
    }

    private void setBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    @Override
    public int getItemCount() {
        return stickerPacksFiltred.size();
    }

    void setMaxNumberOfStickersInARow(int maxNumberOfStickersInARow) {
        if (this.maxNumberOfStickersInARow != maxNumberOfStickersInARow) {
            this.maxNumberOfStickersInARow = maxNumberOfStickersInARow;
            notifyDataSetChanged();
        }
    }

    public void setStickerPackList(List<Sticker_packs> stickerPackList) {
        this.stickerPacks = stickerPackList;
        this.stickerPacksFiltred = stickerPackList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    stickerPacksFiltred = stickerPacks;
                } else {
                    List<Sticker_packs> filteredList = new ArrayList<Sticker_packs>();
                    for (Sticker_packs row : stickerPacks) {
                        if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    stickerPacksFiltred = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = stickerPacksFiltred;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                stickerPacksFiltred = (List<Sticker_packs>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnAddButtonClickedListener {
        void onAddButtonClicked(Sticker_packs stickerPack);
    }

    public interface OnContainerLayoutClickedListener {
        void onContainerLayoutClicked(Sticker_packs stickerPack);
    }
}