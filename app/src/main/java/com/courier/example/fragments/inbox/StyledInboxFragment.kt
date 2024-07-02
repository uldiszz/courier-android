package com.courier.example.fragments.inbox

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.courier.android.Courier
import com.courier.android.models.markAsRead
import com.courier.android.models.markAsUnread
import com.courier.android.ui.CourierStyles
import com.courier.android.ui.inbox.CourierInbox
import com.courier.android.ui.inbox.CourierInboxTheme
import com.courier.example.Env
import com.courier.example.R
import com.courier.example.Theme
import com.courier.example.fragments.DetailSheet
import com.courier.example.toJson

class StyledInboxFragment : Fragment(R.layout.fragment_styled_inbox) {

    private lateinit var inbox: CourierInbox

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inbox = view.findViewById(R.id.courierInbox)

        val theme = CourierInboxTheme(
            brandId = Env.COURIER_BRAND_ID,
            unreadIndicatorStyle = CourierStyles.Inbox.UnreadIndicatorStyle(
                indicator = CourierStyles.Inbox.UnreadIndicator.DOT,
                color = Theme.getPrimaryColor(requireContext())
            ),
            titleStyle = CourierStyles.Inbox.TextStyle(
                unread = CourierStyles.Font(
                    typeface = Theme.getFont(requireContext()),
                ),
                read = CourierStyles.Font(
                    typeface = Theme.getFont(requireContext()),
                ),
            ),
            bodyStyle = CourierStyles.Inbox.TextStyle(
                unread = CourierStyles.Font(
                    typeface = Theme.getFont(requireContext()),
                    color = Theme.getSubtitleColor(requireContext()),
                ),
                read = CourierStyles.Font(
                    typeface = Theme.getFont(requireContext()),
                    color = Theme.getSubtitleColor(requireContext()),
                )
            ),
            timeStyle = CourierStyles.Inbox.TextStyle(
                unread = CourierStyles.Font(
                    typeface = Theme.getFont(requireContext()),
                    sizeInSp = Theme.getSmallFontSize()
                ),
                read = CourierStyles.Font(
                    typeface = Theme.getFont(requireContext()),
                    color = Theme.getSubtitleColor(requireContext()),
                    sizeInSp = Theme.getSmallFontSize()
                )
            ),
            infoViewStyle = Theme.getInfoViewStyles(requireContext()),
            buttonStyle = CourierStyles.Inbox.ButtonStyle(
                unread = Theme.getButton(requireContext()),
                read = Theme.getButton(requireContext())
            ),
            dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

        inbox.lightTheme = theme
        inbox.darkTheme = theme

        inbox.setOnClickMessageListener { message, index ->
            val str = message.toJson() ?: "Invalid"
            Courier.log(str)
            if (!message.isRead) DetailSheet(str).show(childFragmentManager, null)
            if (message.isRead) message.markAsUnread() else message.markAsRead()
        }

        inbox.setOnClickActionListener { action, message, index ->
            val str = action.toJson() ?: "Invalid"
            Courier.log(str)
            DetailSheet(str).show(childFragmentManager, null)
        }

        inbox.setOnScrollInboxListener { offsetInDp ->
            Courier.log(offsetInDp.toString())
        }

    }

}