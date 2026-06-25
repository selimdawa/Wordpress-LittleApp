package com.littleapp.wordpress.Sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.littleapp.wordpress.Model.Post

class PostDB private constructor(context: Context) {

    private val dbHelper = TodoItemDbHelper(context.applicationContext)

    object PostItem : BaseColumns {
        const val TABLE_NAME = "post"
        const val COLNAME_POSTID = "postID"
        const val COLNAME_TITLE = "title"
        const val COLNAME_EXCERPT = "excerpt"
        const val COLNAME_ISFAV = "isFavorite"
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
                    arrayOf(BaseColumns._ID, PostItem.COLNAME_POSTID, PostItem.COLNAME_TITLE, PostItem.COLNAME_EXCERPT, PostItem.COLNAME_ISFAV),
                    null, null, null, null, null
                ).use { cursor ->
                    while (cursor.moveToNext()) {
                        postList.add(
                            Post(
                                sqLiteId = cursor.getInt(0),
                                wpPostId = cursor.getInt(1),
                                wpTitle = cursor.getString(2),
                                wpExcerpt = cursor.getString(3),
                                isFavorite = cursor.getInt(4) == 1
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
                arrayOf(PostItem.COLNAME_ISFAV),
                "${PostItem.COLNAME_POSTID} = ?",
                arrayOf(postID.toString()),
                null, null, null
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
                put(PostItem.COLNAME_POSTID, wpPostID)
                put(PostItem.COLNAME_TITLE, wpTitle)
                put(PostItem.COLNAME_EXCERPT, wpExcerpt)
                put(PostItem.COLNAME_ISFAV, if (isFavorite) 1 else 0)
            }
            db.insert(PostItem.TABLE_NAME, null, values)
        }
    }

    fun update(post: Post): Int {
        return dbHelper.writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(PostItem.COLNAME_TITLE, post.wpTitle)
                put(PostItem.COLNAME_EXCERPT, post.wpExcerpt)
                put(PostItem.COLNAME_ISFAV, if (post.isFavorite) 1 else 0)
            }
            db.update(
                PostItem.TABLE_NAME,
                values,
                "${BaseColumns._ID} = ?",
                arrayOf(post.id.toString())
            )
        }
    }

    fun delete(postID: Int): Int {
        return dbHelper.writableDatabase.use { db ->
            db.delete(
                PostItem.TABLE_NAME,
                "${PostItem.COLNAME_POSTID} = ?",
                arrayOf(postID.toString())
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

        private const val SQL_CREATE_ENTRIES = "CREATE TABLE ${PostItem.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${PostItem.COLNAME_POSTID} INT," +
                "${PostItem.COLNAME_TITLE} TEXT," +
                "${PostItem.COLNAME_EXCERPT} TEXT," +
                "${PostItem.COLNAME_ISFAV} TINYINT(1))"
    }
}