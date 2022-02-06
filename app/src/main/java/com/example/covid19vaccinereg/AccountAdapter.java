package com.example.covid19vaccinereg;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountHolder> {

    Context mCtx;
    int layoutRes;
    List<Account> accountList;
    DatabaseManager mDatabase;
    SessionManager sm;

    //modified the constructor and we are taking the DatabaseManager instance here
    public AccountAdapter(Context mCtx, int layoutRes, List<Account> accountList, DatabaseManager mDatabase) {
        //super(mCtx, layoutRes, accountList);
        this.mCtx = mCtx;
        this.layoutRes = layoutRes;
        this.accountList = accountList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    //Initialize the account adapter
    public AccountAdapter.AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the layout for the recycler view
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        View view = inflater.inflate(R.layout.user_layout_view,parent, false);
        return new AccountHolder(view);
    }

    //bind the view to the data from the database
    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.AccountHolder holder, int position) {
        final Account account = accountList.get(position);
        holder.vUserID.setText(String.valueOf(account.getId()));
        holder.vUsername.setText(account.getUsername());
        //if account is normal user
        if(account.getRole()==0)
        {
            //set the text to normal user
            holder.vUserType.setText("Normal User");
        }
        else//if account is admin
        {
            //set text to admin
            holder.vUserType.setText("Admin");
        }
        if(account.getIsRegistered()==0)//if account is not registered for the vaccine
        {
            holder.vUserRegister.setText("This user has not yet registered for the vaccine");
            holder.ViewMoreInfo.setEnabled(false);
        }
        else//if account is registered for the vaccine
        {
            holder.vUserRegister.setText("This User is already registered for the vaccine");
        }
        //set on click listerner for the view more infor button
        holder.ViewMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass the values to the next activity to view the users information
                Intent intent = new Intent(mCtx,ViewRegisteredUserInfo.class);
                intent.putExtra("Uid",String.valueOf(accountList.get(position).getId()));
                intent.putExtra("Uname",accountList.get(position).getUsername());
                intent.putExtra("URole",String.valueOf(accountList.get(position).getRole()));
                intent.putExtra("isRegistered",String.valueOf(accountList.get(position).getIsRegistered()));
                v.getContext().startActivity(intent);
            }
        });
        String UserType = holder.vUserType.getText().toString();
        //check the usertype and set the text for the change user button accordingly
        if (UserType.equalsIgnoreCase("Admin"))
        {
            holder.changeType.setText("Change to User");
        }
        else
        {
            holder.changeType.setText("Change to Admin");
        }
        //set on click listener to the button to change user type
        holder.changeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check the type of user based on the text of the button clicked
                //if normal user
                if (holder.changeType.getText().toString().equalsIgnoreCase("Change to Admin"))
                {
                    //change user role in database to admin (role = 1)
                    mDatabase.makeAdmin(String.valueOf(accountList.get(position).getId()));
                    holder.changeType.setText("Change to User");
                    //reload all accounts from database
                    LoadAccountsFromDatabase();

                }
                else //if user is an admin
                {
                    //change user to normal user in database (role = 1)
                    mDatabase.makeNormalUser(String.valueOf(accountList.get(position).getId()));
                    holder.changeType.setText("Change to Admin");
                    //reload all accounts from database
                    LoadAccountsFromDatabase();
                }
            }
        });


    }

    //get the size of the list
    @Override
    public int getItemCount() {
        return accountList.size();
    }

    //create view holder class to create variables for each view in the recycler view
    public class AccountHolder extends RecyclerView.ViewHolder{
        TextView vUserID,vUsername,vUserType,vUserRegister;
        Button ViewMoreInfo, changeType;

        //initialize variables for each item of the recycler view
        public AccountHolder(@NonNull View itemView) {
            super(itemView);
            vUserID=itemView.findViewById(R.id.tvUserID);
            vUsername= itemView.findViewById(R.id.tvUsername);
            vUserType = itemView.findViewById(R.id.tvUserType);
            vUserRegister = itemView.findViewById(R.id.tvUserIsRegistered);
            ViewMoreInfo = itemView.findViewById(R.id.btnViewMoreUserInfo);
            changeType = itemView.findViewById(R.id.btnChangeUserType);

        }
    }
    //used to notify when the dataset is changed and to reload all accounts from database
    private void LoadAccountsFromDatabase() {
        sm=new SessionManager(mCtx.getApplicationContext());
        Cursor cursor = mDatabase.getAllAccounts(sm.getUsername());

        if (cursor.moveToFirst()) {
            accountList.clear();
            do {
                accountList.add(new Account(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        } else {
            accountList.clear();
        }
        notifyDataSetChanged();
    }
}