package com.huynhngoctai.vpn_ict.view.frgment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.huynhngoctai.vpn_ict.adapter.AppInfoAdapter
import com.huynhngoctai.vpn_ict.databinding.FragmentAppUseVpnBinding
import com.huynhngoctai.vpn_ict.model.AppInfo

@Suppress("DEPRECATION")
class AppUseVpnFragment : BaseFragment<FragmentAppUseVpnBinding>() {
    companion object {
        val TAG: String = AppUseVpnFragment::class.qualifiedName!!
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAppUseVpnBinding {

        return FragmentAppUseVpnBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        addEvent()
        val appsList = fetchApps()
        val adapter = AppInfoAdapter(appsList)
        binding.recyclerViewApps.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewApps.adapter = adapter
    }

    private fun addEvent() {
        binding.ibtBackCoin.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        animationView(v)

        when (v) {
            binding.ibtBackCoin -> goBackMain()
        }
    }

    private fun goBackMain() {
        callBack.showFragment(MainFragment.TAG, false)
    }

    private fun fetchApps(): List<AppInfo> {
        val apps = mutableListOf<AppInfo>()
        val packageManager = context?.packageManager ?: return apps

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            val permissions: Array<String>
            try {
                permissions = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
                if (permissions.contains(Manifest.permission.INTERNET)) {
                    val appName = resolveInfo.loadLabel(packageManager).toString()
                    val appIcon = resolveInfo.loadIcon(packageManager)
                    apps.add(AppInfo(appName, appIcon, false))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return apps
    }
}