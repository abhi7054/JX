package com.xtreme.jx.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xtreme.jx.model.SearchHistory;
import com.xtreme.jx.utils.Constant;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class ORMLiteHelper extends OrmLiteSqliteOpenHelper {

    private ORMLiteHelper dbHelper = null;

    public ORMLiteHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, SearchHistory.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public Dao.CreateOrUpdateStatus createOrUpdate(SearchHistory obj) throws SQLException {
        Dao<SearchHistory, ?> dao = (Dao<SearchHistory, ?>) getDao(obj.getClass());
        return dao.createOrUpdate(obj);
    }

    public List getAll(Class clazz) throws SQLException {
        Dao<SearchHistory, ?> dao = getDao(clazz);
        return dao.queryForAll();
    }
}
