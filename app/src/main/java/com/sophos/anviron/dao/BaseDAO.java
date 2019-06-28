package com.sophos.anviron.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

import com.sophos.anviron.models.Detection;

@Dao
public interface BaseDAO<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(T...t);

    @Update
    public void update(T...t);

    @Delete
    public void delete(T...t);
}
