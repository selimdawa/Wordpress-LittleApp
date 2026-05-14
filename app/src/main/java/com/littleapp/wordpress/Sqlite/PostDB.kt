package com.littleapp.wordpress.Sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.littleapp.wordpress.Model.Post

class PostDB(private val context: Context?) {

    object PostItem : BaseColumns {
        const val TABLE_NAME = "post"
        const val COLNAME_POSTID = "postID"
        const val COLNAME_TITLE = "title"
        const val COLNAME_EXCERPT = "excerpt"
        const val COLNAME_ISFAV = "isFavorite"
    }

    inner class TodoItemDbHelper(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    }

    val allDbPosts: List<Post>
        get() {
            val helper = TodoItemDbHelper(
                context
            )
            val sqLiteDatabase: SQLiteDatabase = helper.readableDatabase
            val postList: ArrayList<Post> = ArrayList()

            try {
                val cursor: Cursor = sqLiteDatabase.query(
                    PostItem.TABLE_NAME,
                    arrayOf(
                        BaseColumns._ID, PostItem.COLNAME_POSTID, PostItem.COLNAME_TITLE,
                        PostItem.COLNAME_EXCERPT, PostItem.COLNAME_ISFAV
                    ),
                    null, null, null, null, null
                )
                try {
                    while (cursor.moveToNext()) {
                        val tmpPost = Post(
                            cursor.getInt(0), cursor.getInt(1),
                            cursor.getString(2), cursor.getString(3),
                            cursor.getInt(4)
                        )
                        postList.add(tmpPost)
                    }
                } finally {
                    cursor.close()
                }
            } finally {
                sqLiteDatabase.close()
            }
            return postList
        }

    fun getDbPostIsFav(postID: Int): Boolean {
        val helper = TodoItemDbHelper(context)
        val sqLiteDatabase: SQLiteDatabase = helper.readableDatabase
        val postList: ArrayList<Post> = ArrayList()
        var isFavorite = false

        try {
            val cursor: Cursor = sqLiteDatabase.query(
                PostItem.TABLE_NAME,
                arrayOf(
                    BaseColumns._ID, PostItem.COLNAME_POSTID,
                    PostItem.COLNAME_TITLE, PostItem.COLNAME_EXCERPT, PostItem.COLNAME_ISFAV
                ),
                null, null, null, null, null
            )
            try {
                while (cursor.moveToNext()) {
                    val tmpPost = Post(
                        cursor.getInt(0), cursor.getInt(1),
                        cursor.getString(2), cursor.getString(3),
                        cursor.getInt(4)
                    )
                    postList.add(tmpPost)
                }
            } finally {
                cursor.close()
            }
        } finally {
            sqLiteDatabase.close()
        }
        for (post in postList) {
            if (post.wpPostId === postID) {
                Log.d("SelectedItem", post.title.toString())
                isFavorite = post.isFavorite
            }
        }
        return isFavorite
    }

    fun insert(wpPostID: Int, wpTitle: String?, wpExcerpt: String?, isFavorite: Boolean): Long {
        //Data heraus holen aus der DB
        val helper = TodoItemDbHelper(context)
        val db: SQLiteDatabase = helper.readableDatabase

        return try {
            val values = ContentValues()
            //Zuordnung spalten und values
            values.put(PostItem.COLNAME_POSTID, wpPostID)
            values.put(PostItem.COLNAME_TITLE, wpTitle)
            values.put(PostItem.COLNAME_EXCERPT, wpExcerpt)
            values.put(PostItem.COLNAME_ISFAV, isFavorite)
            db.insert(PostItem.TABLE_NAME, PostItem.COLNAME_TITLE, values)
        } finally {
            db.close()
        }
    }

    fun update(post: Post): Int {
        val helper = TodoItemDbHelper(context)
        val db: SQLiteDatabase = helper.writableDatabase

        return try {
            val values = ContentValues()
            values.put(PostItem.COLNAME_TITLE, post.wpTitle)
            values.put(PostItem.COLNAME_EXCERPT, post.wpExcerpt)
            values.put(PostItem.COLNAME_ISFAV, post.isFavorite)

            //Ist die ID die ID aus dem Post Objekt
            val whereClause: String = BaseColumns._ID + " LIKE ?"
            val whereArgs = arrayOf<String>(java.lang.String.valueOf(post.id))
            db.update(PostItem.TABLE_NAME, values, whereClause, whereArgs)
        } finally {
            db.close()
        }
    }

    fun delete(postID: Int): Int {
        val helper = TodoItemDbHelper(context)
        val db: SQLiteDatabase = helper.writableDatabase

        return try {
            val whereClause = PostItem.COLNAME_POSTID + " LIKE ?"
            val whereArgs = arrayOf(postID.toString())
            db.delete(PostItem.TABLE_NAME, whereClause, whereArgs)
        } finally {
            db.close()
        }
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Post.db"
        private var myInstance: PostDB? = null

        fun getInstance(context: Context?): PostDB? {
            if (myInstance == null) {
                myInstance = PostDB(context)
            }
            return myInstance
        }

        //String zum erstellen der DB
        private val SQL_CREATE_ENTRIES = "CREATE TABLE " + PostItem.TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PostItem.COLNAME_POSTID + " INT," +
                PostItem.COLNAME_TITLE + " TEXT," +
                PostItem.COLNAME_EXCERPT + " TEXT," +
                PostItem.COLNAME_ISFAV + " TINYINT(1)" + ")"

        //String zum entfernen der DB
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PostItem.TABLE_NAME
    }
}