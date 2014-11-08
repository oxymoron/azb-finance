package com.azubkov.azbfinance;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.azubkov.azbfinance.data.FinContract.AccountEntry;

import java.util.Currency;

/**
 * Created by oxymoron on 02.10.2014.
 */
public class AccountAdapter extends CursorAdapter {

    public AccountAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private static class ViewHolder {
        public final ImageView iconView;
        public final TextView bankView;
        public final TextView amountView;
        public final TextView convertedAmountView;

        private ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.image_view_account_logo);
            bankView = (TextView) view.findViewById(R.id.text_view_bank);
            amountView = (TextView) view.findViewById(R.id.text_view_amount);
            convertedAmountView = (TextView) view.findViewById(R.id.text_view_converted_amount);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view =
                LayoutInflater.from(context).inflate(R.layout.list_item_account, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        double amount = cursor.getDouble(cursor.getColumnIndex(AccountEntry.COLUMN_AMOUNT));
        String bank = cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_BANK));
        String currency = cursor.getString(cursor.getColumnIndex(AccountEntry.COLUMN_CURRENCY));

        Curr curr = Curr.valueOf(currency);
        viewHolder.bankView.setText(bank);
        viewHolder.amountView.setText(Util.toCurr(context, amount, curr));

        Curr targetCurr = Curr.RUB;
        double targetAmount = Util.convert(amount, curr, targetCurr);
        viewHolder.convertedAmountView.setText(Util.toCurr(context, targetAmount, targetCurr));

        if (bank.contains("Cбер")){
            viewHolder.iconView.setImageResource(R.drawable.ic_sb_logo);
        } else if (bank.contains("KEB")){
            viewHolder.iconView.setImageResource(R.drawable.ic_keb_logo);
        } else {
            viewHolder.iconView.setImageResource(R.drawable.ic_account);
        }
    }
}