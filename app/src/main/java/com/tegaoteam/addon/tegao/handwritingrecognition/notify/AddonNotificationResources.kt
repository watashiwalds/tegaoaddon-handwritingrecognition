package com.tegaoteam.addon.tegao.handwritingrecognition.notify

import com.tegaoteam.addon.tegao.handwritingrecognition.AddonApplication
import com.tegaoteam.addon.tegao.handwritingrecognition.R

class AddonNotificationResources {
    companion object {
        val appIconResId = R.drawable.ic_launcher_foreground
        const val notifyChannelId = "addon_important_notifyChannel"
        val channelName = AddonApplication.instance.getString(R.string.addon_important_notifyChannel_name)
        val channelDesc = AddonApplication.instance.getString(R.string.addon_important_notifyChannel_desc)
        val appContext by lazy { AddonApplication.instance }


        val rmbToUninstall_label = AddonApplication.instance.getString(R.string.addon_notify_rememberToUninstall_label)
        val rmbToUninstall_content = AddonApplication.instance.getString(R.string.addon_notify_rememberToUninstall_content)
    }
}