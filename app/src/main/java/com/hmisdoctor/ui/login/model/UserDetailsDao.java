package com.hmisdoctor.ui.login.model;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.hmisdoctor.ui.login.model.login_response_model.UserDetails;


@Dao
public abstract class UserDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertUserDetails(UserDetails userDetailsResponseContent);

    @Query("SELECT * FROM UserDetails")
    public abstract UserDetails getUserDetails();

    @Query("DELETE FROM UserDetails")
    abstract void deleteUserDetails();

    @Transaction
    public void deleteAndInsert(UserDetails userDetails){
        deleteUserDetails();
        insertUserDetails(userDetails);
    }
}