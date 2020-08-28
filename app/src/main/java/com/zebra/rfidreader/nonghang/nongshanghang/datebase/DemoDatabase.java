package com.zebra.rfidreader.nonghang.nongshanghang.datebase;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.zebra.rfidreader.nonghang.application.Application;
import com.zebra.rfidreader.nonghang.nongshanghang.shinv.FileBean;

@Database(entities = {
        FileBean.class
}
        , version = 2)
@TypeConverters(DateConverter.class)
public abstract class DemoDatabase extends RoomDatabase {
    public static final String DB_NAME = "inventory.db";
    private static volatile DemoDatabase instance;

    public static synchronized DemoDatabase getInstance() {
        if (instance == null) {
            instance = createDb();
        }
        return instance;
    }

    private static DemoDatabase createDb() {
        DemoDatabase build = Room.databaseBuilder(
                Application.getInstance(),
                DemoDatabase.class,
                DB_NAME).addCallback(new RoomDatabase.Callback() {
            //第一次创建数据库时调用，但是在创建所有表之后调用的
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }

            //当数据库被打开时调用
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }
        }).allowMainThreadQueries().addMigrations(MIGRATION_1_2).build();
        return build;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //此处对于数据库中的所有更新都需要写下面的代码
            database.execSQL("ALTER TABLE FileBean "
                    + " ADD COLUMN remarkNum TEXT");
        }
    };

    public abstract FileBeanDao getFileBeanDao();
}