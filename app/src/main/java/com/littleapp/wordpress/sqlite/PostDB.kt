package com.littleapp.wordpress.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.littleapp.wordpress.model.Post

class PostDB private constructor(context: Context) {

    private val dbHelper = TodoItemDbHelper(context.applicationContext)

    object PostItem : BaseColumns {
        const val TABLE_NAME = "post"
        const val COLUMN_POST_ID = "postID"
        const val COLUMN_TITLE = "title"
        const val COLUMN_EXCERPT = "excerpt"
        const val COLUMN_IS_FAVORITE = "isFavorite"
    }

    private class TodoItemDbHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    }

    val allDbPosts: List<Post>
        get() {
            val postList = ArrayList<Post>()
            dbHelper.readableDatabase.use { db ->
                db.query(
                    PostItem.TABLE_NAME,
                    arrayOf(
                        BaseColumns._ID,
                        PostItem.COLUMN_POST_ID,
                        PostItem.COLUMN_TITLE,
                        PostItem.COLUMN_EXCERPT,
                        PostItem.COLUMN_IS_FAVORITE
                    ),
                    null,
                    null,
                    null,
                    null,
                    null,
                ).use { cursor ->
                    while (cursor.moveToNext()) {
                        postList.add(
                            Post(
                                sqLiteId = cursor.getInt(0),
                                wpPostId = cursor.getInt(1),
                                wpTitle = cursor.getString(2),
                                wpExcerpt = cursor.getString(3),
                                isFavorite = cursor.getInt(4) == 1,
                            )
                        )
                    }
                }
            }
            return postList
        }

    fun getDbPostIsFav(postID: Int): Boolean {
        var isFavorite = false
        dbHelper.readableDatabase.use { db ->
            db.query(
                PostItem.TABLE_NAME,
                arrayOf(PostItem.COLUMN_IS_FAVORITE),
                "${PostItem.COLUMN_POST_ID} = ?",
                arrayOf(postID.toString()),
                null,
                null,
                null
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    isFavorite = cursor.getInt(0) == 1
                }
            }
        }
        return isFavorite
    }

    fun insert(wpPostID: Int, wpTitle: String?, wpExcerpt: String?, isFavorite: Boolean): Long {
        return dbHelper.writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(PostItem.COLUMN_POST_ID, wpPostID)
                put(PostItem.COLUMN_TITLE, wpTitle)
                put(PostItem.COLUMN_EXCERPT, wpExcerpt)
                put(PostItem.COLUMN_IS_FAVORITE, if (isFavorite) 1 else 0)
            }
            db.insert(PostItem.TABLE_NAME, null, values)
        }
    }

    fun delete(postID: Int): Int {
        return dbHelper.writableDatabase.use { db ->
            db.delete(
                PostItem.TABLE_NAME, "${PostItem.COLUMN_POST_ID} = ?", arrayOf(postID.toString())
            )
        }
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Post.db"

        @Volatile
        private var myInstance: PostDB? = null

        fun getInstance(context: Context?): PostDB? {
            if (context == null) return null
            return myInstance ?: synchronized(this) {
                myInstance ?: PostDB(context).also { myInstance = it }
            }
        }

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${PostItem.TABLE_NAME} (" + "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," + "${PostItem.COLUMN_POST_ID} INT," + "${PostItem.COLUMN_TITLE} TEXT," + "${PostItem.COLUMN_EXCERPT} TEXT," + "${PostItem.COLUMN_IS_FAVORITE} TINYINT(1))"
    }
}