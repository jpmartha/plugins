package io.flutter.plugins.webviewflutter;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import androidx.annotation.Nullable;
import io.flutter.plugin.common.PluginRegistry;

import android.provider.MediaStore;
import java.io.File;
import android.os.Environment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import androidx.core.content.FileProvider;

public class FlutterWebViewChromeClient extends WebChromeClient
    implements PluginRegistry.ActivityResultListener {
  private static final int REQUEST_CODE_FILE_CHOOSER = 0x12;

  private String currentPhotoPath;

  private ValueCallback<Uri[]> filePathCallback;

  private PluginRegistry.Registrar registrar;

  public FlutterWebViewChromeClient(PluginRegistry.Registrar registrar) {
    super();
    this.registrar = registrar;
    registrar.addActivityResultListener(this);
  }

  @Override
  public void onProgressChanged(WebView view, int newProgress) {
    super.onProgressChanged(view, newProgress);
  }

  @Override
  public void onReceivedTitle(WebView view, String title) {
    super.onReceivedTitle(view, title);
  }

  @Override
  public void onReceivedIcon(WebView view, Bitmap icon) {
    super.onReceivedIcon(view, icon);
  }

  @Override
  public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
    super.onReceivedTouchIconUrl(view, url, precomposed);
  }

  @Override
  public void onShowCustomView(View view, CustomViewCallback callback) {
    super.onShowCustomView(view, callback);
  }

  @Override
  public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
    super.onShowCustomView(view, requestedOrientation, callback);
  }

  @Override
  public void onHideCustomView() {
    super.onHideCustomView();
  }

  @Override
  public boolean onCreateWindow(
      WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
  }

  @Override
  public void onRequestFocus(WebView view) {
    super.onRequestFocus(view);
  }

  @Override
  public void onCloseWindow(WebView window) {
    super.onCloseWindow(window);
  }

  @Override
  public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
    return super.onJsAlert(view, url, message, result);
  }

  @Override
  public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
    return super.onJsConfirm(view, url, message, result);
  }

  @Override
  public boolean onJsPrompt(
      WebView view, String url, String message, String defaultValue, JsPromptResult result) {
    return super.onJsPrompt(view, url, message, defaultValue, result);
  }

  @Override
  public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
    return super.onJsBeforeUnload(view, url, message, result);
  }

  @Override
  public void onExceededDatabaseQuota(
      String url,
      String databaseIdentifier,
      long quota,
      long estimatedDatabaseSize,
      long totalQuota,
      WebStorage.QuotaUpdater quotaUpdater) {
    super.onExceededDatabaseQuota(
        url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
  }

  @Override
  public void onReachedMaxAppCacheSize(
      long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
    super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
  }

  @Override
  public void onGeolocationPermissionsShowPrompt(
      String origin, GeolocationPermissions.Callback callback) {
    super.onGeolocationPermissionsShowPrompt(origin, callback);
  }

  @Override
  public void onGeolocationPermissionsHidePrompt() {
    super.onGeolocationPermissionsHidePrompt();
  }

  @Override
  public void onPermissionRequest(PermissionRequest request) {
    super.onPermissionRequest(request);
  }

  @Override
  public void onPermissionRequestCanceled(PermissionRequest request) {
    super.onPermissionRequestCanceled(request);
  }

  @Override
  public boolean onJsTimeout() {
    return super.onJsTimeout();
  }

  @Override
  public void onConsoleMessage(String message, int lineNumber, String sourceID) {
    super.onConsoleMessage(message, lineNumber, sourceID);
  }

  @Override
  public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
    return super.onConsoleMessage(consoleMessage);
  }

  @Nullable
  @Override
  public Bitmap getDefaultVideoPoster() {
    return super.getDefaultVideoPoster();
  }

  @Nullable
  @Override
  public View getVideoLoadingProgressView() {
    return super.getVideoLoadingProgressView();
  }

  @Override
  public void getVisitedHistory(ValueCallback<String[]> callback) {
    super.getVisitedHistory(callback);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  public boolean onShowFileChooser(
      WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
    this.filePathCallback = filePathCallback;
    // Gallary
    Intent intent = fileChooserParams.createIntent();
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    // Camera
    File photoFile = null;

    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = registrar.activity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    try {
      photoFile = File.createTempFile(
              imageFileName,  /* prefix */
              ".jpg",         /* suffix */
              storageDir      /* directory */
      );
    } catch (IOException ex) {
      ex.printStackTrace();
      return false;
    }

    if (photoFile == null) {
      return false;
    }

    Uri uri = FileProvider.getUriForFile(
            registrar.activity(),
            getApplicationContext().getPackageName() + ".fileprovider",
            photoFile);
    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    currentPhotoPath = uri.toString();

    // Chooser
    Intent chooserIntent = Intent.createChooser(intent, "Picture...");
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

    try {
      registrar.activity().startActivityForResult(chooserIntent, REQUEST_CODE_FILE_CHOOSER);
    } catch (ActivityNotFoundException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_FILE_CHOOSER
        && (resultCode == RESULT_OK || resultCode == RESULT_CANCELED)) {

      Uri[] results = null;

      if (data == null) {
        if (currentPhotoPath != null) {
          results = new Uri[]{Uri.parse(currentPhotoPath)};
          filePathCallback.onReceiveValue(results);
          return false;
        }
      } else {
        String dataString = data.getDataString();
        if (dataString != null) {
          results = new Uri[]{Uri.parse(dataString)};
        } else if (currentPhotoPath != null) {
          // For Android8
          results = new Uri[]{Uri.parse(currentPhotoPath)};
        }
        filePathCallback.onReceiveValue(results);
        return false;
      }

      filePathCallback.onReceiveValue(
          WebChromeClient.FileChooserParams.parseResult(resultCode, data));
    }
    return false;
  }
}
