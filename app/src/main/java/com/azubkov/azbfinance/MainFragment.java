package com.azubkov.azbfinance;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.azubkov.azbfinance.data.FinContract;
import com.azubkov.azbfinance.data.FinContract.AccountEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ACCOUNT_LOADER = 0;
    private CursorAdapter adapter;

    private TextView mTotal;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        adapter = new AccountAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_accounts);
        listView.setAdapter(adapter);

        mTotal = (TextView) rootView.findViewById(R.id.text_view_total_value);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ACCOUNT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                AccountEntry.CONTENT_URI,
                new String[]{
                        AccountEntry._ID,
                        AccountEntry.COLUMN_CURRENCY,
                        AccountEntry.COLUMN_AMOUNT,
                        AccountEntry.COLUMN_BANK
                },
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
        double sum = 0;
        Curr targetCurr = Curr.RUB;
        while (cursor.moveToNext()){
            double amount = cursor.getDouble(cursor.getColumnIndex(AccountEntry.COLUMN_AMOUNT));
            String currStr = cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_CURRENCY));
            Curr curr = Curr.valueOf(currStr);
            double converted = Util.convert(amount, curr, targetCurr);
            sum += converted;
        }
        mTotal.setText(Util.toCurr(getActivity(), sum, targetCurr));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }
}
