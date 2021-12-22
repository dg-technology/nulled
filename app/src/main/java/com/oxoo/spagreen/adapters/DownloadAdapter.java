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
import com.oxoo.spagreen.models.CommonModels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.OriginalViewHolder> {
    private static final String TAG = "DownloadAdapter";
    private static final int DOWNLOAD_JOB_KEY = 101;
    private List<CommonModels> items = new ArrayList<>();
    private Activity context;
    private Context mcontext;
    private boolean isDialog;
    private View v = null;
    private DownloadViewModel viewModel;



    public DownloadAdapter(Activity context, List<CommonModels> items, boolean isDialog, DownloadViewModel viewModel) {
        this.context = context;
        this.items = items;
        this.isDialog = isDialog;
        this.viewModel = viewModel;
    }

    @Override
    public OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OriginalViewHolder vh;
        if (isDialog){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_download_item_vertical, parent, false);
        }else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_download_item, parent, false);
        }
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final OriginalViewHolder holder, final int position) {


        final CommonModels obj = items.get(position);
        holder.name.setText(obj.getTitle());
        holder.resolution.setText(obj.getResulation() + "," );
        holder.size.setText(obj.getFileSize());

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (obj.isInAppDownload()) {
                    //in app download enabled
                    //startDownloadWithNotification(obj.getStremURL(), obj.getTitle());

                    /*DownloadHelper helper = new DownloadHelper(obj.getTitle(),
                            obj.getStremURL(),
                            context,
                            viewModel
                    );
                    helper.downloadFile();

                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(obj.getStremURL());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setVisibleInDownloadsUi(true);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
                    downloadManager.enqueue(request);
*/
                    SharedPreferences editor = context.getSharedPreferences("title", Context.MODE_PRIVATE);

                    File folder = new File(Environment.getExternalStorageDirectory() +"/Android/data/" + context.getPackageName() +"/files/.sys");

                    if (!folder.exists()) {
                       folder.mkdirs();
                    }

                    String filepath = Environment.getExternalStorageDirectory() +"/Android/data/" + context.getPackageName() +"/files/.sys";

                    String mtitle = editor.getString("title", null)+obj.getTitle();

                    Toast.makeText(context,"Waiting...",Toast.LENGTH_SHORT).show();
                    holder.itemLayout.setEnabled(false);
                    xdownloader(mtitle,filepath,obj.getStremURL(),v);

                } else {
                    String url = obj.getStremURL();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }



    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name, resolution, size;
        public LinearLayout itemLayout;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            resolution = v.findViewById(R.id.resolution_tv);
            size = v.findViewById(R.id.size_tv);
            itemLayout=v.findViewById(R.id.item_layout);
        }
    }

    public void downloadfile(String filename,String filepath,String download_link,View v){

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
            filename = filename.substring((int)(0), (int)(50)).concat(".sys");
        }
        else {
            filename = filename.concat(".sys");
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
        private static String getBytesToMBString(long bytes){
            return String.format(Locale.ENGLISH, "℅.2fMB", bytes / (1024.00 * 1024.00));
        }
    }

    public void xdownloader(String title, String filepath, String durl,View v){

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

                downloadfile(title,filepath,vidURL.get(0).getUrl(),v);

            }

            @Override
            public void onError() {

                downloadfile(title,filepath,durl,v);
            }
        });
        xGetter.find(durl);
    }

}