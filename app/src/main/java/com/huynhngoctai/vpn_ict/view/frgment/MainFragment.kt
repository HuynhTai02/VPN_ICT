package com.huynhngoctai.vpn_ict.view.frgment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.net.VpnService
import android.os.RemoteException
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
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.databinding.FragmentMainBinding
import com.huynhngoctai.vpn_ict.model.ServerOpenVpn
import com.huynhngoctai.vpn_ict.model.ServerWire
import com.huynhngoctai.vpn_ict.service.TimerService
import com.huynhngoctai.vpn_ict.util.CommonUtils
import com.huynhngoctai.vpn_ict.util.QuantityFormatter
import com.huynhngoctai.vpn_ict.view.OnDialogListenerVerifyAds
import com.huynhngoctai.vpn_ict.view.dialog.CheckInDailyDialog
import com.huynhngoctai.vpn_ict.view.dialog.RatingDialog
import com.huynhngoctai.vpn_ict.view.dialog.VerifyAdsDialog
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import com.wireguard.config.Interface
import com.wireguard.config.Peer
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Timer
import java.util.TimerTask

class MainFragment : BaseFragment<FragmentMainBinding>(), OnDialogListenerVerifyAds,
    NavigationView.OnNavigationItemSelectedListener {
    companion object {
        val TAG: String = MainFragment::class.qualifiedName!!
    }

    private var myTunnel = App.get().getMyTunnel()
    private var myConfig = App.get().getMyConfig()
    private var isStopwatchRunning = false
    private lateinit var statusReceiver: BroadcastReceiver
    private lateinit var timeReceiver: BroadcastReceiver
    private var serverWire: ServerWire? = null
    private var serverOpenVpn: ServerOpenVpn? = null

    private val permissionActivityResultLauncherTypeWire =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                Toast.makeText(
                    requireActivity(),
                    "You need to grant VPN Master permission",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                setTunnelState(Tunnel.State.UP)
            }
        }

    private val permissionActivityResultLauncherTypeOpen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                Toast.makeText(
                    requireActivity(),
                    "You need to grant VPN Master permission",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                startVpn()
            }
        }

    override fun initViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        serverWire = getSharedServer()
        serverOpenVpn = getSharedServerOpenVpn()
        showDiaNoInternet()
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
        binding.tvTapToDisconnect.setOnClickListener(this)
        binding.ibtArrowDown.setOnClickListener(this)

        //C1
        binding.navigationView.setNavigationItemSelectedListener(this)
        //C2
//        handleNavigationView()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding.ibtWatchCoin -> showDialogWatchCoin()
            binding.ibtDailyCoin -> showDialogDailyCheckIn()
            binding.ibtDrawerMenu -> openDrawerMenu()
            binding.ibtArrowDown -> {
                animationView(v)
                goToListSeverFragment()
            }

            binding.tvTapToConnect -> {
                if (binding.tvNameFlag.text.isNullOrEmpty()) {
                    notify("Please select a country first!")
                } else {
                    if (CommonUtils.getPrefString("type").equals("OpenVpn")) {

                        setViewTunnelStateUp()
                    } else {
                        startStopwatch()
                        setTunnelState(Tunnel.State.UP)
                    }
                }
            }

            binding.tvTapToDisconnect -> {
                if (CommonUtils.getPrefString("type").equals("OpenVpn")) {
                    OpenVPNThread.stop()
                    setViewTunnelStateDown()
                } else {
                    stopStopwatch()
                    setTunnelState(Tunnel.State.DOWN)
                }
            }
        }
    }

    private fun startVpn() {
        val intent = VpnService.prepare(activity)
        if (intent!=null){
            permissionActivityResultLauncherTypeOpen.launch(intent)
        }

        Log.d(TAG, "startVpn: ....")
        try {
            // .ovpn file
            val conf = requireActivity().assets.open(serverOpenVpn!!.configOpenVpn)
            val isr = InputStreamReader(conf)
            val br = BufferedReader(isr)
            var config = ""
            var line: String?
            while (true) {
                line = br.readLine()
                if (line == null) break
                config += """
                $line

                """.trimIndent()
            }
            br.readLine()
            OpenVpnApi.startVpn(
                context,
                config,
                serverOpenVpn?.nameCity,
                serverOpenVpn?.userOpenVpn,
                serverOpenVpn?.passwordOpenVpn
            )

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun setTunnelState(stateTunnel: Tunnel.State) {
        lifecycleScope.launch {
            try {
                val intent = GoBackend.VpnService.prepare(activity)
                if (intent != null) {
                    permissionActivityResultLauncherTypeWire.launch(intent)
                    return@launch
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            setTunnelStateWithPermissionsResultBuilder(stateTunnel)
        }
    }

    private fun setTunnelStateWithPermissionsResultBuilder(stateTunnel: Tunnel.State) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.ivFlag.setImageResource(serverWire!!.flagCountry)
                    binding.tvNameFlag.text = serverWire?.nameCity
                }

                val myInterface = Interface.Builder()
                    .parsePrivateKey(serverWire?.privateKeyInterface.toString())
                    .parseAddresses(serverWire?.addressInterface.toString())
                    .parseDnsServers(serverWire?.dnsInterface.toString())
                    .build()

                val myPear = Peer.Builder()
                    .parsePublicKey(serverWire?.publicKeyPeer.toString())
                    .parseEndpoint(serverWire?.endpointPeer.toString())
                    .parseAllowedIPs(serverWire?.allowedIPsPeer.toString())
                    .build()

                myConfig = Config.Builder()
                    .setInterface(myInterface)
                    .addPeer(myPear)
                    .build()

                if (myTunnel == null) {
                    myTunnel = object : Tunnel {
                        override fun getName() = "ICT"
                        override fun onStateChange(newState: Tunnel.State) {
                            Log.d("onStateChange", newState.toString())

                            updateUIOnTunnelState(newState)
                        }
                    }
                }

                App.getBackend().setState(
                    myTunnel!!, stateTunnel, myConfig
                )

            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun getSharedServer(): ServerWire {
        val flagCountry = CommonUtils.getPrefInt("flagCountry")
        val nameCountry = CommonUtils.getPrefString("nameCountry") ?: ""
        val privateKeyInterface = CommonUtils.getPrefString("privateKeyInterface") ?: ""
        val addressInterface = CommonUtils.getPrefString("addressInterface") ?: ""
        val dnsInterface = CommonUtils.getPrefString("dnsInterface") ?: ""
        val publicKeyPeer = CommonUtils.getPrefString("publicKeyPeer") ?: ""
        val endpointPeer = CommonUtils.getPrefString("endpointPeer") ?: ""
        val allowedIPsPeer = CommonUtils.getPrefString("allowedIPsPeer") ?: ""

        return ServerWire(
            nameCountry,
            flagCountry,
            privateKeyInterface,
            addressInterface,
            dnsInterface,
            publicKeyPeer,
            allowedIPsPeer,
            endpointPeer
        )
    }

    private fun getSharedServerOpenVpn(): ServerOpenVpn {
        val flagOpen = CommonUtils.getPrefInt("flagOpen")
        val nameOpen = CommonUtils.getPrefString("nameCityOpen") ?: ""
        val configOpen = CommonUtils.getPrefString("configOpen") ?: ""
        val userOpen = CommonUtils.getPrefString("userOpen") ?: ""
        val passOpen = CommonUtils.getPrefString("passOpen") ?: ""

        return ServerOpenVpn(
            nameOpen,
            flagOpen,
            configOpen,
            userOpen,
            passOpen
        )
    }

    private fun updateUIOnTunnelState(newState: Tunnel.State) {
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("updateUIOnTunnelState: ", newState.toString())

            if (newState == Tunnel.State.UP) {
                setViewTunnelStateUp()
            } else if (newState == Tunnel.State.DOWN) {
                setViewTunnelStateDown()
            }
        }
    }

    private fun setViewTunnelStateUp() {
        lifecycleScope.launch(Dispatchers.Main) {
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
            binding.ltaConnect.setAnimation(R.raw.connected)
            binding.ltaConnect.playAnimation()
            loadRxTxVPN()
        }
    }

    private fun setViewTunnelStateDown() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.tvTapToConnect.visibility = View.VISIBLE
            binding.cvFlag.visibility = View.VISIBLE
            binding.tvTapToDisconnect.visibility = View.GONE
            binding.ivDownload.visibility = View.GONE
            binding.tvDownload.visibility = View.GONE
            binding.tvCountKbDownload.visibility = View.GONE
            binding.ivUpload.visibility = View.GONE
            binding.tvUpload.visibility = View.GONE
            binding.tvCountKbUpload.visibility = View.GONE
            binding.tvTxtTime.visibility = View.GONE
            binding.tvDisplayTime.visibility = View.GONE
            binding.ltaConnect.setAnimation(R.raw.connecting)
            binding.ltaConnect.playAnimation()
        }
    }

    private fun loadRxTxVPN() {
        val updateTimerLoadRxTx = Timer()
        updateTimerLoadRxTx.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                lifecycleScope.launch(Dispatchers.IO) {
                    val rx = App.getBackend().getStatistics(myTunnel!!).totalRx()
                    val tx = App.getBackend().getStatistics(myTunnel!!).totalTx()

                    lifecycleScope.launch(Dispatchers.Main) {

                        binding.tvCountKbUpload.text =
                            context?.getString(
                                R.string.transfer_tx,
                                QuantityFormatter.formatBytes(tx)
                            )

                        binding.tvCountKbDownload.text =
                            context?.getString(
                                R.string.transfer_rx,
                                QuantityFormatter.formatBytes(rx)
                            )
                    }
                }
            }
        }, 1000, 1000)
    }

    private fun getStopwatchStatus() {
        val timerService = Intent(requireActivity(), TimerService::class.java)
        timerService.putExtra(TimerService.STOPWATCH_ACTION, TimerService.GET_STATUS)
        requireActivity().startService(timerService)
    }

    private fun startStopwatch() {
        val timerService = Intent(requireActivity(), TimerService::class.java)
        timerService.putExtra(TimerService.STOPWATCH_ACTION, TimerService.START)
        requireActivity().startService(timerService)
    }

    private fun stopStopwatch() {
        val timerService = Intent(requireActivity(), TimerService::class.java)
        timerService.putExtra(TimerService.STOPWATCH_ACTION, TimerService.STOP)
        requireActivity().startService(timerService)
    }

    private fun moveToForeground() {
        val timerService = Intent(requireActivity(), TimerService::class.java)
        timerService.putExtra(TimerService.STOPWATCH_ACTION, TimerService.MOVE_TO_FOREGROUND)
        requireActivity().startService(timerService)
    }

    private fun moveToBackground() {
        val timerService = Intent(requireActivity(), TimerService::class.java)
        timerService.putExtra(TimerService.STOPWATCH_ACTION, TimerService.MOVE_TO_BACKGROUND)
        requireActivity().startService(timerService)
    }

    @SuppressLint("SetTextI18n")
    private fun updateStopwatchValue(timeElapsed: Int) {
        val hours: Int = (timeElapsed / 60) / 60
        val minutes: Int = timeElapsed / 60
        val seconds: Int = timeElapsed % 60
        binding.tvDisplayTime.text =
            "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

    private fun updateLayout(isStopwatchRunning: Boolean) {
        if (isStopwatchRunning) {
            setTunnelStateWithPermissionsResultBuilder(Tunnel.State.UP)
        } else {
            setTunnelStateWithPermissionsResultBuilder(Tunnel.State.DOWN)
        }
    }

    private fun showDialogDailyCheckIn() {
        val dailyCheckInDialog = CheckInDailyDialog(requireContext())
        dailyCheckInDialog.isResetDailyCoinCheckIn = true
        dailyCheckInDialog.show()
    }

    private fun showDialogWatchCoin() {
        val verifyAdsDialog = VerifyAdsDialog(requireContext())
        verifyAdsDialog.isResetDailyCoinWatchAds = true
        verifyAdsDialog.setOnDialogListener(this)
        verifyAdsDialog.show()
    }

    override fun showVideoAdsFragment() {
        binding.rlOverlay.visibility = View.VISIBLE
        binding.ltaProgressbar.visibility = View.VISIBLE
        binding.ltaProgressbar.playAnimation()

        loadVideoAds()
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
        callBack.showFragment(AppUseVpnFragment.TAG, false)
    }

    private fun goToListSeverFragment() {
        callBack.showFragment(SeverListFragment.TAG, false)
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

    //Read File Lib Wire Guard VPN
//    private fun setTunnelStateWithPermissionsResult() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                FileInputStream(File(requireActivity().filesDir, "a.conf")).use { stream ->
//                    val config = Config.parse(stream)
//                    val newState = withContext(Dispatchers.IO) {
//                        myTunnel = object : Tunnel {
//                            override fun getName() = "ICT"
//                            override fun onStateChange(newState: Tunnel.State) {
//                                Log.d("onStateChange", newState.toString())
//                            }
//                        }
//
//                        App.getBackend().setState(
//                            myTunnel!!, Tunnel.State.UP, config
//                        )
//                    }
//
//                    Log.d("setTunnelState", newState.toString())
//                }
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            }
//        }
//    }

//        private fun handleNavigationView() {
//                binding.navigationView.setNavigationItemSelectedListener {
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
//    }

    override fun onStart() {
        super.onStart()

        moveToBackground()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()

        getStopwatchStatus()

        // Receiving stopwatch status from service
        val statusFilter = IntentFilter()
        statusFilter.addAction(TimerService.STOPWATCH_STATUS)
        statusReceiver = object : BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            override fun onReceive(p0: Context?, p1: Intent?) {
                val isRunning = p1?.getBooleanExtra(TimerService.IS_STOPWATCH_RUNNING, false)!!
                isStopwatchRunning = isRunning
                val timeElapsed = p1.getIntExtra(TimerService.TIME_ELAPSED, 0)

                updateLayout(isStopwatchRunning)
                updateStopwatchValue(timeElapsed)
            }
        }
        requireActivity().registerReceiver(statusReceiver, statusFilter)

        // Receiving time values from service
        val timeFilter = IntentFilter()
        timeFilter.addAction(TimerService.STOPWATCH_TICK)
        timeReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val timeElapsed = p1?.getIntExtra(TimerService.TIME_ELAPSED, 0)!!
                updateStopwatchValue(timeElapsed)
            }
        }
        requireActivity().registerReceiver(timeReceiver, timeFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(statusReceiver)
        requireActivity().unregisterReceiver(timeReceiver)

        // Moving the service to foreground when the app is in background / not visible
        moveToForeground()
    }
}