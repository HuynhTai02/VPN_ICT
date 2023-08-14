package com.huynhngoctai.vpn_ict.view.frgment

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.material.navigation.NavigationView
import com.huynhngoctai.vpn_ict.App
import com.huynhngoctai.vpn_ict.Constants
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.databinding.FragmentMainBinding
import com.huynhngoctai.vpn_ict.view.OnDialogListenerMain
import com.huynhngoctai.vpn_ict.view.dialog.CheckInDailyDialog
import com.huynhngoctai.vpn_ict.view.dialog.RatingDialog
import com.huynhngoctai.vpn_ict.view.dialog.VerifyAdsDialog
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import com.wireguard.config.Interface
import com.wireguard.config.Peer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

class MainFragment : BaseFragment<FragmentMainBinding>(), OnDialogListenerMain,
    NavigationView.OnNavigationItemSelectedListener {
    companion object {
        val TAG: String = MainFragment::class.qualifiedName!!
    }

    private val permissionActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            setTunnelState(Tunnel.State.UP)
        }

    override fun initViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        showBannerAds()
        addEvents()
    }

    private fun showBannerAds() {
        val adRequest = AdRequest.Builder().build()
        binding.adBanner.loadAd(adRequest)
    }

    private fun addEvents() {
        binding.ibtWatchCoin.setOnClickListener(this)
        binding.ibtDailyCoin.setOnClickListener(this)
        binding.ibtDrawerMenu.setOnClickListener(this)
        binding.tvTapToConnect.setOnClickListener(this)
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.tvTapToDisconnect.setOnClickListener(this)

//        binding.navigationView.setNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.nav_policy -> goToWebViewPolicy()
//                R.id.nav_app_vpn -> goToAppUseVpnFragment()
//                R.id.nav_more_coin -> goToMoreCoinFragment()
//                R.id.nav_rate -> showDialogRateUs()
//                R.id.nav_tell_friends -> shareApp()
//                R.id.nav_list_sever -> goToListSeverFragment()
//                else -> helpUsImprove()
//            }
//
//            closeDrawerMenu()
//            false
//        }
    }

    override fun clickView(v: View) {
        super.clickView(v)
        when (v) {
            binding.ibtWatchCoin -> showDialogWatchCoin()
            binding.ibtDailyCoin -> showDialogDailyCheckIn()
            binding.ibtDrawerMenu -> openDrawerMenu()
            binding.tvTapToConnect -> setTunnelState(Tunnel.State.UP)
            binding.tvTapToDisconnect -> setTunnelState(Tunnel.State.DOWN)
        }
    }

    private fun setTunnelState(stateTunnel: Tunnel.State) {
        lifecycleScope.launch {
            try {
                val intent = GoBackend.VpnService.prepare(activity)
                if (intent != null) {
                    permissionActivityResultLauncher.launch(intent)
                    return@launch
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                Toast.makeText(
                    requireActivity(),
                    e.printStackTrace().toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            setTunnelStateWithPermissionsResultBuilder(stateTunnel)
        }
    }

    //Read File
    private fun setTunnelStateWithPermissionsResult() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                FileInputStream(File(requireActivity().filesDir, "a.conf")).use { stream ->
                    val config = Config.parse(stream)
                    val newState = withContext(Dispatchers.IO) {
                        App.getBackend().setState(
                            object : Tunnel {
                                override fun getName() = "ICT"
                                override fun onStateChange(newState: Tunnel.State) {
                                    Log.d("onStateChange", newState.toString())
                                }
                            }, Tunnel.State.UP, config
                        )
                    }
                    Log.d("setTunnelState", newState.toString())
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun setTunnelStateWithPermissionsResultBuilder(stateTunnel: Tunnel.State) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val interfaze = Interface.Builder()
                    .parsePrivateKey(Constants.privateKeyInterface)
                    .parseAddresses(Constants.addressInterface)
                    .parseDnsServers(Constants.dnsInterface)
                    .build()

                val pear = Peer.Builder()
                    .parsePublicKey(Constants.publicKeyPeer)
                    .parsePreSharedKey(Constants.preSharedKeyPeer)
                    .parseEndpoint(Constants.endpointPeer)
                    .parseAllowedIPs(Constants.allowedIPsPeer)
                    .build()

                val config = Config.Builder()
                    .setInterface(interfaze)
                    .addPeer(pear)
                    .build()

                val newState = withContext(Dispatchers.IO) {
                    App.getBackend().setState(
                        object : Tunnel {
                            override fun getName() = "ICT"
                            override fun onStateChange(newState: Tunnel.State) {
                                Log.d("onStateChange", newState.toString())

                                updateUIOnTunnelState(newState)
                            }
                        }, stateTunnel, config
                    )
                }

                Log.d("setTunnelState", newState.toString())

            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUIOnTunnelState(newState: Tunnel.State) {
        if (newState == Tunnel.State.UP){
            binding.tvTapToConnect.visibility = View.GONE
            binding.cvFlag.visibility = View.GONE

            binding.tvTapToDisconnect.visibility = View.VISIBLE
            binding.ivDownload.visibility = View.VISIBLE
            binding.tvDownload.visibility = View.VISIBLE
            binding.tvCountKbDownload.visibility = View.VISIBLE
            binding.ivUpload.visibility = View.VISIBLE
            binding.tvUpload.visibility = View.VISIBLE
            binding.tvCountKbUpload.visibility = View.VISIBLE
            binding.tvTxtTime.visibility = View.VISIBLE
            binding.tvDisplayTime.visibility = View.VISIBLE
        }
    }

    private fun showDialogDailyCheckIn() {
        val dailyCheckInDialog = CheckInDailyDialog(requireContext())
        dailyCheckInDialog.show()
    }

    private fun showDialogWatchCoin() {
        showDiaNoInternet()
        val verifyAdsDialog = VerifyAdsDialog(requireContext())
        verifyAdsDialog.setOnDialogListener(this)
        verifyAdsDialog.show()
    }

    override fun showVideoAdsFragment() {
        binding.rlOverlay.visibility = View.VISIBLE
        binding.ltaProgressbar.visibility = View.VISIBLE
        binding.ltaProgressbar.playAnimation()

        loadVideoAds("User earned the reward 100 coin.")
    }

    override fun isClickableView() {
        binding.ltaProgressbar.visibility = View.GONE
        binding.rlOverlay.visibility = View.GONE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_policy -> goToWebViewPolicy()
            R.id.nav_app_vpn -> goToAppUseVpnFragment()
            R.id.nav_more_coin -> goToMoreCoinFragment()
            R.id.nav_rate -> showDialogRateUs()
            R.id.nav_tell_friends -> shareApp()
            R.id.nav_list_sever -> goToListSeverFragment()
            else -> sendFeedbackHelpUsImprove()
        }

        closeDrawerMenu()
        return false
    }

    private fun openDrawerMenu() {
        binding.drawerMenu.openDrawer(binding.navigationView)
    }

    private fun closeDrawerMenu() {
        binding.drawerMenu.closeDrawer(binding.navigationView)
    }

    private fun showDialogRateUs() {
        val dialogRating = RatingDialog(requireContext())
        dialogRating.show()
    }

    private fun shareApp() {
        val appUrl = "https://github.com/HuynhTai02"
        val shareIntent = Intent(Intent.ACTION_SEND)

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Link VPN Master")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Thank you for sharing my app with everyone!!\n\nLink: $appUrl"
        )
        shareIntent.type = "text/plain"

        startActivity(shareIntent)
    }

    private fun sendFeedbackHelpUsImprove() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:hnt8102@gmail.com")
        }
        startActivity(intent)
    }

    private fun goToAppUseVpnFragment() {
        //do nothing
    }

    private fun goToListSeverFragment() {
        //do nothing
    }

    private fun goToMoreCoinFragment() {
        callBack.showFragment(CoinFragment.TAG, false)
    }

    private fun goToWebViewPolicy() {
        callBack.showFragment(PolicyFragment.TAG, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.adBanner.destroy()
    }
}