package my.app.cookbox.sqlite;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class RecipeSyncAdapter extends AbstractThreadedSyncAdapter {

    public RecipeSyncAdapter(
            Context context,
            boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public RecipeSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

    }
}
