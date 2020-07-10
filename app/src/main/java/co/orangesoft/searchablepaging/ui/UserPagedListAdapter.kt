package co.orangesoft.searchablepaging.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.orangesoft.searchablepaging.R
import co.orangesoft.searchablepaging.models.User
import kotlinx.android.synthetic.main.user_item.view.*

class UserPagedListAdapter(diffUtilCallback: DiffUtil.ItemCallback<User>): PagedListAdapter<User, UserPagedListAdapter.UserViewHolder>(diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class UserViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        fun bind(user: User?) {
            itemView.tvLogin.text = "${user?.id}. ${user?.login}"
        }
    }
}