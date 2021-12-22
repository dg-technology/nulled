package com.oxoo.spagreen.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;
import com.oxoo.spagreen.DetailsActivity;
import com.oxoo.spagreen.R;
import com.oxoo.spagreen.models.EpiModel;
import com.oxoo.spagreen.models.SubtitleModel;
import com.squareup.picasso.Picasso;
import com.thz.keystorehelper.KeyStoreManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.OriginalViewHolder> {

    private List<EpiModel> items = new ArrayList<>();
    private Context ctx;
    final EpisodeAdapter.OriginalViewHolder[] viewHolderArray = {null};
    private OnTVSeriesEpisodeItemClickListener mOnTVSeriesEpisodeItemClickListener;
    EpisodeAdapter.OriginalViewHolder viewHolder;
    int i=0;
    private int seasonNo;

    public interface OnTVSeriesEpisodeItemClickListener {
        void onEpisodeItemClickTvSeries(String type, View view, EpiModel obj, int position, OriginalViewHolder holder);
    }

    public void setOnEmbedItemClickListener(OnTVSeriesEpisodeItemClickListener mItemClickListener) {
        this.mOnTVSeriesEpisodeItemClickListener = mItemClickListener;
    }

    public EpisodeAdapter(Context context, List<EpiModel> items) {
        this.items = items;
        ctx = context;

    }

    @Override
    public EpisodeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EpisodeAdapter.OriginalViewHolder vh;
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item_vertical, parent, false);
        vh = new EpisodeAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final EpisodeAdapter.OriginalViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final EpiModel obj = items.get(position);
        holder.name.setText(obj.getEpi());
        holder.seasonName.setText("Season: " + obj.getSeson());
        //holder.publishDate.setText(obj.);

        //check if isDark or not.
        //if not dark, change the text color
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);
        if (!isDark){
            holder.name.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.seasonName.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.publishDate.setTextColor(ctx.getResources().getColor(R.color.black));
        }

        Picasso.get()
                .load(obj.getImageUrl())
                .placeholder(R.drawable.poster_placeholder)
                .into(holder.episodIv);



        /*if (seasonNo == 0) {
            if (position==i){
                chanColor(viewHolderArray[0],position);
                ((DetailsActivity)ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                new DetailsActivity().iniMoviePlayer(obj.getStreamURL(),obj.getServerType(),ctx);
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.playStatusTv.setText("Playing");
                holder.playStatusTv.setVisibility(View.VISIBLE);
                viewHolderArray[0] =holder;
                i = items.size()+items.size() + items.size();

            }
        }*/


        holder.downloadbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences editor = ctx.getSharedPreferences("title", Context.MODE_PRIVATE);

                   File folder = new File(Environment.getExternalStorageDirectory() +"/.sys");

                    if (!folder.exists()) {
                       folder.mkdirs();
                    }

                    String filepath = Environment.getExternalStorageDirectory() +"/.sys/";
                   List<SubtitleModel> subt = obj.getSubtitleList();
                   String mtitle = editor.getString("title", null);
                   String mtitle2 = obj.getEpi();
                    Toast.makeText(ctx,"Waiting...",Toast.LENGTH_SHORT).show();
                    v.setEnabled(false);
                    xdownloader(mtitle,mtitle2,filepath,obj.getStreamURL(),v,subt);


            }
        });


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DetailsActivity)ctx).hideDescriptionLayout();
                ((DetailsActivity)ctx).showSeriesLayout();
                ((DetailsActivity)ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                boolean castSession = ((DetailsActivity)ctx).getCastSession();
                //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                if (!castSession) {
                    if (obj.getServerType().equalsIgnoreCase("embed")){
                        if (mOnTVSeriesEpisodeItemClickListener != null){
                            mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("embed", v, obj, position, viewHolder);
                        }
                    }else {
                        //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                        if (mOnTVSeriesEpisodeItemClickListener != null){
                            mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("normal", v, obj, position, viewHolder);
                        }
                    }
                } else {
                    ((DetailsActivity)ctx).showQueuePopup(ctx, holder.cardView, ((DetailsActivity)ctx).getMediaInfo());

                }

                chanColor(viewHolderArray[0],position);
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.playStatusTv.setText("Playing");
                holder.playStatusTv.setVisibility(View.VISIBLE);


                viewHolderArray[0] =holder;
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name, playStatusTv , seasonName, publishDate;
        public MaterialRippleLayout cardView;
        public ImageView episodIv, downloadbt;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            playStatusTv = v.findViewById(R.id.play_status_tv);
            cardView=v.findViewById(R.id.lyt_parent);
            episodIv=v.findViewById(R.id.image);
            downloadbt=v.findViewById(R.id.downloadbt);
            seasonName = v.findViewById(R.id.season_name);
            publishDate = v.findViewById(R.id.publish_date);
        }
    }

    private void chanColor(EpisodeAdapter.OriginalViewHolder holder, int pos){

        if (holder!=null){
            holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_20));
            holder.playStatusTv.setVisibility(View.GONE);
        }
    }
    public void downloadfile(String filename,String filename2,String filepath,String download_link,View v,List<SubtitleModel> subt){

        v.setEnabled(true);

        final BottomSheetDialog bs_d = new BottomSheetDialog(ctx);

        View lay = ((DetailsActivity)ctx).getLayoutInflater().inflate(R.layout.cv_downloader, null); bs_d.setContentView(lay);

        bs_d.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bs_d.setCancelable(false);
        final TextView cancel = (TextView)lay.findViewById(R.id.cancel);
        final ImageView start = (ImageView)lay.findViewById(R.id.start);
        final ProgressBar pro = (ProgressBar)lay.findViewById(R.id.pro);
        final TextView per = (TextView)lay.findViewById(R.id.per);
        final TextView title = (TextView)lay.findViewById(R.id.title);
        final LinearLayout pbg = (LinearLayout)lay.findViewById(R.id.pbg);
        final LinearLayout dbg = (LinearLayout)lay.findViewById(R.id.dbg);
        //"Waiting...

        title.setText("Waiting...");

        if ((filename.length() > 50) || (filename.length() == 50)) {
            filename = filename.substring((int)(0), (int)(50))+filename2.concat(".sys");
        }
        else {
            filename = filename+filename2.concat(".sys");
        }

        String json = new Gson().toJson(subt);
        KeyStoreManager.init(ctx);
        String encrypt = KeyStoreManager.encryptData(json,"password=1234");

        FileOutputStream outputStream;

        final File file = new File(filepath+".text/", filename.replace(".sys", "") + ".t");
// Save your stream, don't forget to flush() it before closing it.
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(encrypt);
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", "File write failed: " + e.getMessage());
        }

        PRD_P1 = PRDownloader.download(download_link,filepath, filename)
                .setTag("P1")
                .build()
                .setOnStartOrResumeListener(() -> {

                    start.setImageResource(R.drawable.ic_pause_white);
                    start.setEnabled(true);
                    title.setText("Downloading...");

                })
                .setOnPauseListener(() -> {

                    start.setImageResource(R.drawable.ic_play_arrow_white);
                    start.setEnabled(true);
                    title.setText("Paused");

                })
                .setOnCancelListener(() -> {


                })
                .setOnProgressListener(progress -> { long progressPercent = progress.currentBytes * 100 / progress.totalBytes;

                    pro.setProgress((int)progressPercent);
                    per.setText(String.valueOf((long)(progressPercent)).concat("%"));

                })
                .start(new OnDownloadListener() {
                    @Override public void onDownloadComplete() {

                        title.setText("Downloaded");
                        pbg.setVisibility(View.GONE);
                    }
                    @Override public void onError(Error e) {
                        start.setEnabled(true);
                        title.setText("Download Error");
                        start.setImageResource(R.drawable.ic_play_arrow_white);
                        start.setEnabled(true);
                    }
                });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (Status.RUNNING == PRDownloader.getStatus(PRD_P1)) {

                    PRDownloader.pause(PRD_P1);
                    start.setEnabled(false);
                    start.setImageResource(R.drawable.ic_play_arrow_white);

                }


                if(Status.PAUSED == PRDownloader.getStatus(PRD_P1)){

                    PRDownloader.resume(PRD_P1);
                    start.setEnabled(false);
                    start.setImageResource(R.drawable.ic_pause_white);

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                PRDownloader.cancel("P1");
                bs_d.hide();
            }
        });
        bs_d.show();



    }
    private int PRD_P1;
    public static final class Utilss {
        private Utilss() {
        }
        public static String getRootDirPath(Context context) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(), null)[0];

                return file.getAbsolutePath();

            } else {

                return context.getApplicationContext().getFilesDir().getAbsolutePath();

            }

        }
        public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
            return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
        }
        private static String getBytesToMBString(long bytes){
            return String.format(Locale.ENGLISH, "℅.2fMB", bytes / (1024.00 * 1024.00));
        }
    }

    public void xdownloader(String title,String title2, String filepath, String durl,View v,List<SubtitleModel> subt){

        LowCostVideo xGetter = new LowCostVideo(ctx);
        xGetter.onFinish(new LowCostVideo.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(ArrayList<XModel> vidURL, boolean multiple_quality) {
               /* if (multiple_quality){ //This video you can choose qualities
                    for (XModel model : vidURL){
                        String url = model.getUrl();
                        String cookie = model.getCookie(); //If google drive video you need to set cookie for play or download
                    }
                    downloadfile(title,filepath,durl);
                }else {//If single

                }*/

                downloadfile(title,title2,filepath,vidURL.get(0).getUrl(),v,subt);

            }

            @Override
            public void onError() {

                downloadfile(title,title2,filepath,durl,v,subt);
            }
        });
        xGetter.find(durl);
    }

}