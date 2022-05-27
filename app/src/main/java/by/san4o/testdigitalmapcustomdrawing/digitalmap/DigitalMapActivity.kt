package by.san4o.testdigitalmapcustomdrawing.digitalmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import by.san4o.testdigitalmapcustomdrawing.R
import by.san4o.testdigitalmapcustomdrawing.databinding.FragmentAddBinding
import by.san4o.testdigitalmapcustomdrawing.databinding.FragmentDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DigitalMapActivity : AppCompatActivity() {

    private var digitalMapCustomView: DigitalMapCustomView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContentView(R.layout.activity_digitalmap_view)
        setContentView(R.layout.activity_digitalmap_custom)

        val view = findViewById<DigitalMapCustomView>(R.id.customView)
            .also { digitalMapCustomView = it }
        view.onElementSelected = { el ->

            ElementDetailsBottomFragment.show(
                supportFragmentManager,
                ElementDetails(
                    color = el.color,
                    name = el.name
                )
            ).apply {
                this.onSaveClick = { details ->
                    view.setDetails(details)
                }
                this.onRemoveClick = {
                    view.removeSelected()
                }
                this.onRotateClick = { rotate ->
                    view.rotateSelected(rotate.toFloat())
                }

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.custom, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.addMenu ->
                ElementAddingBottomFragment.show(supportFragmentManager)
                    .apply {
                        onAddClick = { figure ->
                            digitalMapCustomView?.addFigure(figure)
                        }
                    }
            R.id.rotateLeftMenu ->{
                digitalMapCustomView?.rotate(20)
            }
            R.id.rotateRightMenu ->{
                digitalMapCustomView?.rotate(-20)
            }
        }
        return false
    }
}

class ElementDetailsBottomFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentDetailsBinding
    var onSaveClick: (ElementDetails) -> Unit = {}
    var onRemoveClick: () -> Unit = {}
    var onRotateClick: (Int) -> Unit = {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentDetailsBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name") ?: ""
        binding.nameEditText.setText(name)
        val color = arguments?.getString("color")?.let { ElementColor.valueOf(it) } ?: ElementColor.Blue
        binding.color.setSelection(color.ordinal)

        binding.removeButton.setOnClickListener {
            onRemoveClick()
            dismiss()
        }
        binding.saveButton.setOnClickListener {
            onSaveClick(
                ElementDetails(
                    color = binding.color.selectedItemPosition
                        .let { pos -> ElementColor.values().find { it.ordinal == pos } }
                        ?: ElementColor.Black,
                    name = binding.nameEditText.text.toString()
                )
            )
            dismiss()
        }

        binding.rotateLeftButton.setOnClickListener {
            onRotateClick(-45)
            dismiss()
        }
        binding.rotateRightButton.setOnClickListener {
            onRotateClick(+45)
            dismiss()
        }
    }

    companion object {
        const val TAG = "ElementDetailsBottomFragment"
        fun show(fragmentManager: FragmentManager, details: ElementDetails): ElementDetailsBottomFragment {
            return ElementDetailsBottomFragment()
                .apply {
                    arguments = bundleOf(
                        "color" to details.color.name,
                        "name" to details.name
                    )
                }
                .also { it.show(fragmentManager, TAG) }

        }
    }
}

class ElementAddingBottomFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentAddBinding
    var onAddClick: (ElementFigure) -> Unit = {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentAddBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addButton.setOnClickListener {
            val figure = binding.figure.selectedItemPosition.let { pos ->
                ElementFigure.values().find { it.ordinal == pos }
            }
            if (figure != null)
                onAddClick(figure)

            dismiss()
        }
    }

    companion object {
        const val TAG = "ElementAddingBottomFragment"
        fun show(fragmentManager: FragmentManager): ElementAddingBottomFragment {
            return ElementAddingBottomFragment()
                .also { it.show(fragmentManager, TAG) }

        }
    }
}


