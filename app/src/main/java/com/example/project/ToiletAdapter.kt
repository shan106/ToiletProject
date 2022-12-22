package com.example.project



import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ToiletAdapter(var context: Context,var mList: ArrayList<Plaatsen>) : RecyclerView.Adapter<ToiletAdapter.ViewHolder>() {


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val textViewAdres: TextView = itemView.findViewById(R.id.tvadres)
        val textViewGeslacht: TextView = itemView.findViewById(R.id.tvgeslacht)
        val texViewRolstoel : TextView = itemView.findViewById(R.id.tvrolstoel)
        val textViewLuiertafel: TextView = itemView.findViewById(R.id.tvluiertafel)
        val textViewDistance: TextView = itemView.findViewById(R.id.tv_current_distance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.toilet_row_new, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemViewModel = mList[position]
        holder.textViewAdres.text = ItemViewModel.adres
        holder.textViewGeslacht.text = ItemViewModel.geslacht
        when (ItemViewModel.rolstoel){
            true -> holder.texViewRolstoel.text = "ja"
            false -> holder.texViewRolstoel.text = "nee"
        }
        holder.textViewLuiertafel.text = ItemViewModel.luiertafel.toString()

        if(ItemViewModel.distance.equals(""))
            holder.textViewDistance.text = "Current Location Not found!"
        else
            holder.textViewDistance.text ="Distance : " + ItemViewModel.distance + " m"

        holder.itemView.setOnClickListener {
            deletePopUp(mList[position])
        }

    }

    private fun deletePopUp(plaatsen: Plaatsen) {
        AlertDialog.Builder(context)
            .setTitle("Delete entry")
            .setMessage("Are you sure you want to delete this entry?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    val db = DBHelper(context)
                    db.delete(plaatsen.id.toString())
                    mList.remove(plaatsen)
                    notifyDataSetChanged()

                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setFilter(newList: ArrayList<Plaatsen>) {
        mList = ArrayList()
        mList.addAll(newList)
        notifyDataSetChanged()
    }

}