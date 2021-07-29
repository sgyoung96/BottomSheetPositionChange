package com.example.bottomsheet

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.bottom_sheet_edit_my_page_test.*
import kotlinx.android.synthetic.main.item_edit_my_page_test.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class BottomSheetEditMyPageTest : BottomSheetDialogFragment() {

    var adapter: AdapterAdded? = null

    var arrayItemAdded = ArrayList<EditMyPageItem>()
    var arrayItemDeleted = ArrayList<EditMyPageItem>()

    var touchHelperAdded: ItemTouchHelper? = null

    var data: MyPreferenceData? = null
    var orderList: JSONArray? = null

    var touchHelperAddedCallback: TouchHelperAddedCallback? = null

    override fun getTheme(): Int = R.style.BottomSheetDialogRounded

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dlg = super.onCreateDialog(savedInstanceState)
        dlg.setCanceledOnTouchOutside(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dlg)
        }
        return dlg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_edit_my_page_test, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (dialog is BottomSheetDialog) {
            val behaviour = (dialog as BottomSheetDialog).behavior
            behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }
        btnSave.isEnabled = false
        btnClose?.setOnClickListener {
            dismissAllowingStateLoss()
        }

        data = MyPreferenceData(view.context)
        orderList = data?.mypageOder

        btnSave?.setOnClickListener {
            val res = JSONObject()
            getOrderJson().apply {
                res.put("orderList", this)
                data?.mypageOder = this
            }
            dismissAllowingStateLoss()
        }

        for (i in 0 until orderList!!.length()) {
            val item: EditMyPageItem =
                Gson().fromJson(orderList!!.getJSONObject(i).toString(), EditMyPageItem::class.java)
            if (item.display == "Y") arrayItemAdded.add(item) else arrayItemDeleted.add(item)
        }

        Log.d("arrayItemAdded", "${arrayItemAdded.size}") // 5

        adapter = AdapterAdded(arrayItemAdded, arrayItemDeleted)

        adapter?.apply {
            onActionListener = object : AdapterAdded.OnActionListener {
                override fun onDragStart(viewHolder: RecyclerView.ViewHolder, index: Int) {
                    dividerIndex = index
                    touchHelperAdded?.startDrag(viewHolder)
                }

                override fun onBtnEnabled() {
                    btnSave.isEnabled = true
                }

                // override fun onDeleteSwipe(viewHolder: RecyclerView.ViewHolder) {
                // }
            }
            touchHelperAddedCallback = TouchHelperAddedCallback(this).apply {
                touchHelperAdded = ItemTouchHelper(this)
            }
            listAdded?.adapter = this
            touchHelperAdded?.attachToRecyclerView(listAdded)
        }
    }

    private fun getOrderJson(): JSONArray {
        val orderList = JSONArray()
        arrayItemAdded = adapter?.getArrayAddData()!!
        arrayItemDeleted = adapter?.getArrayDeleteData()!!
        arrayItemAdded.forEachIndexed { index, editMyPageItem ->
            editMyPageItem.order = index + 1
            orderList.put(JSONObject(Gson().toJson(editMyPageItem)))
        }
        arrayItemDeleted.forEachIndexed { index, editMyPageItem ->
            editMyPageItem.order = arrayItemAdded.size + index + 1
            orderList.put(JSONObject(Gson().toJson(editMyPageItem)))
        }
        Log.d("Tag", "orderList $orderList")
        return orderList
    }

    class AdapterAdded(
        private var arrayAdded: ArrayList<EditMyPageItem>,
        private var arrayDelete: ArrayList<EditMyPageItem>
    ) : RecyclerView.Adapter<EditViewHolder>(), TouchHelperAddedCallback.OnTouchHelperListener {

        var data: ArrayList<EditMyPageItem> = arrayListOf()
        var dividerIndex: Int = 0

        init {
            if (arrayAdded.size != 0) data.addAll(arrayAdded)
            data.add(EditMyPageItem()) // divider
            if (arrayDelete.size != 0) data.addAll(arrayDelete)

            Log.d("data size", "${data.size}") // 6
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditViewHolder {
            val holder = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_edit_my_page_test, parent, false)
            return EditViewHolder(holder)
        }

        override fun onBindViewHolder(holder: EditViewHolder, position: Int) {
            val item = data[holder.adapterPosition]
            Log.d("item", "${item.name}")

            if (item.display == "") { // divider
                holder.vItem.visibility = View.GONE
                holder.vgSwipe.visibility = View.GONE
                holder.vDivider.visibility = View.VISIBLE
                dividerIndex = position
            } else {
                holder.vItem.visibility = View.VISIBLE
                holder.vDivider.visibility = View.GONE
                if (item.display == "Y") {
                    holder.vgSwipe.visibility = View.VISIBLE
                    holder.btnAdd.visibility = View.INVISIBLE
                    holder.btnDel.visibility = View.VISIBLE
                    holder.divider.visibility =
                        if (holder.adapterPosition == arrayAdded.size - 1) View.INVISIBLE else View.VISIBLE
                } else { // "N"
                    holder.vgSwipe.visibility = View.INVISIBLE
                    holder.btnAdd.visibility = View.VISIBLE
                    holder.btnDel.visibility = View.INVISIBLE
                    holder.divider.visibility =
                        if (holder.adapterPosition == arrayDelete.size - 1) View.INVISIBLE else View.VISIBLE
                }
            }

            holder.tvTitle.text = item.name

            holder.handle.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    onActionListener?.onDragStart(holder, dividerIndex)
                }
                return@setOnTouchListener false
            }
        }

        override fun getItemCount(): Int {
            return arrayAdded.size + arrayDelete.size + 1 // divider
        }

        var onActionListener: OnActionListener? = null

        interface OnActionListener {
            fun onDragStart(viewHolder: RecyclerView.ViewHolder, index: Int)
            fun onBtnEnabled()
            // fun onDeleteSwipe(viewHolder: RecyclerView.ViewHolder)
        }

        override fun onMoving(posFrom: Int, posTo: Int) {
            Collections.swap(data, posFrom, posTo)
            notifyItemMoved(posFrom, posTo)
            onActionListener?.onBtnEnabled()
            dataMovingInit()
        }

        override fun onMoveEnded(isUpDown: Boolean, posFrom: Int, posTo: Int) {
            if (!isUpDown) {
                if (dividerIndex <= posTo) {
                    var item = data[posTo]
                    if (arrayAdded.contains(item)) {
                        // Move Down
                        item.display = "N"
                        arrayDelete.add(posTo - arrayAdded.size, item)
                        arrayAdded.remove(item)
                        adapterDataInit()
                    }
                }
            } else {
                if (dividerIndex >= posTo) {
                    var item = data[posTo]
                    if (arrayDelete.contains(item)) {
                        // Move Up
                        item.display = "Y"
                        arrayAdded.add(posTo, item)
                        arrayDelete.remove(item)
                        adapterDataInit()
                    }
                }
            }
            onActionListener?.onBtnEnabled()
            dividerIndex = arrayAdded.size
        }

        // override fun onItemDismiss(position: Int) {
        // }

        private fun dataMovingInit() {
            arrayAdded = ArrayList()
            arrayDelete = ArrayList()
            for (item in data) {
                if (item.display == "Y") {
                    arrayAdded.add(item)
                } else if (item.display == "N") {
                    arrayDelete.add(item)
                }
            }
        }

        private fun adapterDataInit() {
            data = ArrayList()
            if (arrayAdded.size != 0) data.addAll(arrayAdded)
            data.add(EditMyPageItem())
            if (arrayDelete.size != 0) data.addAll(arrayDelete)
            dividerIndex = 0
            notifyDataSetChanged()
        }

        fun getArrayAddData(): ArrayList<EditMyPageItem> {
            return arrayAdded
        }

        fun getArrayDeleteData(): ArrayList<EditMyPageItem> {
            return arrayDelete
        }
    }

    class EditViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val vItem: ViewGroup = v.findViewById(R.id.vg_item)
        val vgSwipe: ViewGroup = v.findViewById(R.id.vg_swipe)
        val vDivider: ViewGroup = v.findViewById(R.id.vg_divider)
        val btnDel: ImageView = v.findViewById(R.id.btnDel)
        val btnAdd: ImageView = v.findViewById(R.id.btnAdd)
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val handle: ImageView = v.findViewById(R.id.handle)
        val divider: View = v.findViewById(R.id.divider)
        val tvDelete: TextView = v.findViewById(R.id.tv_delete)
    }

    class EditMyPageItem(
        val name: String = "",
        val code: String = "",
        var order: Int = 1000,
        var display: String = ""
    )

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun setWhiteNavigationBar(dialog: Dialog) {
        val window: Window? = dialog.window
        if (window != null) {
            val metrics = DisplayMetrics()
            window.windowManager.defaultDisplay.getMetrics(metrics)
            val dimDrawable = GradientDrawable()
            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(Color.WHITE)
            val layers: Array<Drawable> = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
            val windowBackground = LayerDrawable(layers)
            windowBackground.setLayerInsetTop(1, metrics.heightPixels)
            window.setBackgroundDrawable(windowBackground)
        }
    }

    class TouchHelperAddedCallback(val adapterAdded: AdapterAdded) : ItemTouchHelper.Callback() {
        interface OnTouchHelperListener {
            fun onMoving(posFrom: Int, posTo: Int)
            fun onMoveEnded(isUpDown: Boolean, posFrom: Int, posTo: Int)
            // fun onItemDismiss(position: Int)
        }

        var moveCheck = false
        var currentPosition: Int? = null
        var previousPosition: Int? = null
        var lastFrom = -1
        var lastTo = -1

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val flag = ItemTouchHelper.UP or ItemTouchHelper.DOWN // 1 or 2
            return makeMovementFlags(flag, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return if (moveCheck) false else {
                lastFrom = viewHolder.adapterPosition
                lastTo = target.adapterPosition
                adapterAdded.onMoving(lastFrom, lastTo)
                previousPosition = lastTo
                true
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

        // 호출 순서 -> onSelectedChanged -> onChildDraw -> clearView
        /*
        actionState ->
        0 = 끝난 경우
        1 = swipe
        2 = up/down
         */
        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (actionState == 2) {
                moveCheck = false
                super.onSelectedChanged(viewHolder, actionState)
            } else if (actionState == 0) {
                if (!moveCheck) {
                    currentPosition = lastTo
                    if (lastFrom > lastTo) {
                        // MoveUp : true
                        adapterAdded.onMoveEnded(true, lastFrom, lastTo)
                    } else {
                        // MoveDown : false
                        adapterAdded.onMoveEnded(false, lastFrom, lastTo)
                    }
                    super.onSelectedChanged(viewHolder, actionState)
                }
            }
        }
    }
}