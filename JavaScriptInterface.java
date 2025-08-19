// File: C:\kasir\android\app\src\main\java\com\kasir\app\JavaScriptInterface.java

package com.kasir.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class JavaScriptInterface {
    WebView webView;
    Context context;

    JavaScriptInterface(Context c, WebView view) {
        this.context = c;
        this.webView = view;
    }

    @JavascriptInterface
    public void shareText(String textToShare) {
        Log.d("JavaScriptInterface", "Fungsi shareText dipanggil.");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        context.startActivity(Intent.createChooser(shareIntent, "Bagikan struk via..."));
    }

    @JavascriptInterface
    public void generateAndSharePdf(String fileName, String title) {
        Log.d("JavaScriptInterface", "Fungsi generateAndSharePdf dipanggil.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
                if (printManager == null) {
                    Toast.makeText(context, "Layanan cetak tidak tersedia.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mengambil PrintDocumentAdapter dari WebView
                PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(title);

                // Mendapatkan direktori penyimpanan yang aman
                File downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                if (downloadsDir == null) {
                    Toast.makeText(context, "Direktori penyimpanan tidak tersedia.", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                File file = new File(downloadsDir, fileName + ".pdf");
                
                // Menyiapkan atribut cetak
                PrintAttributes printAttributes = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(new PrintAttributes.Resolution("res1", "PDF_QUALITY", 600, 600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build();

                // Membuat PDF
                printAdapter.onLayout(null, printAttributes, null, new PrintDocumentAdapter.LayoutResultCallback() {
                    @Override
                    public void onLayoutFinished(PrintDocumentAdapter.LayoutResult result) {
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            printAdapter.onWrite(new android.print.PageRange[]{new android.print.PageRange(0, android.print.PageRange.ALL_PAGES)}, fos, null, new PrintDocumentAdapter.WriteResultCallback() {
                                @Override
                                public void onWriteFinished(android.print.PageRange[] pages) {
                                    // Berbagi file PDF setelah berhasil dibuat
                                    Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.setType("application/pdf");
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    context.startActivity(Intent.createChooser(shareIntent, "Bagikan PDF via..."));
                                }
                            });
                        } catch (IOException e) {
                            Log.e("JavaScriptInterface", "Gagal menulis file PDF: " + e.getMessage());
                            Toast.makeText(context, "Gagal menulis file PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, null);
            } catch (Exception e) {
                Log.e("JavaScriptInterface", "Gagal membuat atau membagikan PDF: " + e.getMessage());
                Toast.makeText(context, "Gagal membuat atau membagikan PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Android versi Anda tidak mendukung fitur ini.", Toast.LENGTH_SHORT).show();
        }
    }
}
