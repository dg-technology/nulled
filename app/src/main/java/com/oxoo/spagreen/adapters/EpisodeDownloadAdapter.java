package com.oxoo.spagreen.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;
import com.oxoo.spagreen.R;
import com.oxoo.spagreen.database.downlaod.DownloadViewModel;
import com.oxoo.spagreen.models.single_details.DownloadLink;
import com.oxoo.spagreen.utils.ItemAnimation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EpisodeDownloadAdapter extends RecyclerView.Adapter<EpisodeDownloadAdapter.SeasonDownloadViewModel> {
    private static final String TAG = "SeasonDownloadAdapter";
    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;

    private Activity context;
    private List<DownloadLink> downloadLinks;
    private DownloadViewModel viewModel;

    public EpisodeDownloadAdapter(Activity context, List<DownloadLink> downloadLinks, DownloadViewModel viewModel) {
        this.context = context;
        this.downloadLinks = downloadLinks;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public SeasonDownloadViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.season_download_item,  parent, false);
        return new SeasonDownloadViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonDownloadViewModel holder, int position) {
        if (downloadLinks != null){
            DownloadLink downloadLink = downloadLinks.get(position);
            holder.episodeName.setText(downloadLink.getLabel());
            holder.seasonDownloadLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadLink.isInAppDownload()) {
                        //in app download enabled
                        if (viewModel != null) {
                           /*DownloadHelper helper = new DownloadHelper(
                                    downloadLink.getLabel(),
                                    downloadLink.getDownloadUrl(),
                                    context,
                                    viewModel);
                            helper.downloadFile();*/

                            SharedPreferences editor = context.getSharedPreferences("title", Context.MODE_PRIVATE);

                            File folder = new File(Environment.getExternalStorageDirectory() +"/Android/data/" + context.getPackageName() +"/files/.sys");

                            if (!folder.exists()) {
                                folder.mkdirs();
                            }

                            String filepath = Environment.getExternalStorageDirectory() +"/Android/data/" + context.getPackageName() +"/files/.sys";

                            Toast.makeText(context,"Waiting...",Toast.LENGTH_SHORT).show();

                            holder.seasonDownloadLayout.setEnabled(false);

                            xdownloader(editor.getString("title", null), downloadLink.getLabel(),filepath,downloadLink.getDownloadUrl(),v);

                        }

                    } else {
                        String url = downloadLink.getDownloadUrl();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        context.startActivity(i);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return downloadLinks.size();
    }

    class SeasonDownloadViewModel extends RecyclerView.ViewHolder{
        private TextView episodeName;
        private ImageView downloadImageView;
        private CardView seasonDownloadLayout;

        public SeasonDownloadViewModel(@NonNull View itemView) {
            super(itemView);
            episodeName             = itemView.findViewById(R.id.episodeNameOfSeasonDownload);
            downloadImageView       = itemView.findViewById(R.id.downloadImageViewOfSeasonDownload);
            seasonDownloadLayout    = itemView.findViewById(R.id.seasonDownloadLayout);
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }

        });



        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
    public void downloadfile(String filename, String filename2 ,String filepath,String download_link,View v){

        v.setEnabled(true);

        final BottomSheetDialog bs_d = new BottomSheetDialog(context);

        View lay = context.getLayoutInflater().inflate(R.layout.cv_downloader, null); bs_d.setContentView(lay);

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
            filename = filename.substring((int)(0), (int)(50)) + filename2 + ".sys";
        }
        else {
            filename = filename + filename2 + ".sys";
        }

        PRD_P1 = PRDownloader.download(download_link,filepath, filename)
                .setTag(filename)
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

        private static String getBytesToMBString(long bytes) {
            return String.format(Locale.ENGLISH, "℅.2fMB", bytes / (1024.00 * 1024.00));
        }
    }

    public void xdownloader(String filename, String filename2, String filepath, String durl,View v){

        LowCostVideo xGetter = new LowCostVideo(context);
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

                downloadfile(filename,filename2,filepath,vidURL.get(0).getUrl(),v);

            }

            @Override
            public void onError() {

                downloadfile(filename,filename2,filepath,durl,v);
            }
        });
        xGetter.find(durl);
    }

}
