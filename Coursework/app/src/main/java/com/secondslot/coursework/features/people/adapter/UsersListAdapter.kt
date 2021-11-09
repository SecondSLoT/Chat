package com.secondslot.coursework.features.people.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.secondslot.coursework.databinding.ItemUserBinding
import com.secondslot.coursework.domain.model.User
import com.secondslot.coursework.extentions.loadRoundImage
import com.secondslot.coursework.features.people.ui.OnUserClickListener

class PeopleListAdapter(
    private val listener: OnUserClickListener
) : ListAdapter<User, PeopleListAdapter.UserViewHolder>(UsersComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(holder.absoluteAdapterPosition))
    }

    class UserViewHolder(
        private val binding: ItemUserBinding,
        private val listener: OnUserClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.run {
                userPhoto.loadRoundImage(user.avatarUrl ?: "")
                username.text = user.fullName
                email.text = user.email
            }

            itemView.setOnClickListener { listener.onUserClick(user.userId) }
        }
    }
}

class UsersComparator : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
