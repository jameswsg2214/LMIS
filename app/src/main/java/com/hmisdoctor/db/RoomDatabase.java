package com.hmisdoctor.db;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.hmisdoctor.ui.login.model.UserDetailsDao;
import com.hmisdoctor.ui.login.model.login_response_model.UserDetails;


@Database(entities = {UserDetails.class}, version = 4, exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    private static RoomDatabase INSTANCE = null;

    /**
     * from developers android, made my own singleton
     *
     * @param context
     * @return
     */
    public static RoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDatabase.class, "hmis_doctor_db")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static androidx.room.RoomDatabase.Callback sRoomDatabaseCallback =
            new androidx.room.RoomDatabase.Callback(){
                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                }
            };
    public abstract UserDetailsDao userDetailsDao();

}