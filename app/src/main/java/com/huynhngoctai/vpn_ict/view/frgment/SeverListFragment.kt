package com.huynhngoctai.vpn_ict.view.frgment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.adapter.CountryOpenVpnAdapter
import com.huynhngoctai.vpn_ict.adapter.CountryWireAdapter
import com.huynhngoctai.vpn_ict.databinding.FragmentSeverListBinding
import com.huynhngoctai.vpn_ict.model.CountryOpenVpn
import com.huynhngoctai.vpn_ict.model.CountryWire
import com.huynhngoctai.vpn_ict.model.ServerOpenVpn
import com.huynhngoctai.vpn_ict.model.ServerWire
import com.huynhngoctai.vpn_ict.util.CommonUtils

class SeverListFragment : BaseFragment<FragmentSeverListBinding>() {
    companion object {
        val TAG: String = SeverListFragment::class.qualifiedName!!
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSeverListBinding {

        return FragmentSeverListBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        showDiaNoInternet()
        addEvents()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val adapterSeverWire =
            CountryWireAdapter(
                sampleDataWire(),
                object : CountryWireAdapter.OnServerItemClickListener {
                    override fun onServerItemClicked(serverWire: ServerWire) {
                        goBackMain(serverWire)
                    }
                }
            )

        binding.rvCountriesWire.adapter = adapterSeverWire
        binding.rvCountriesWire.layoutManager = LinearLayoutManager(context)

        val adapterOpenVpn =
            CountryOpenVpnAdapter(
                sampleDataOpenVpn(),
                object : CountryOpenVpnAdapter.OnServerItemClickListener {
                    override fun onServerItemClicked(serverOpen: ServerOpenVpn) {
                        goBackMain(serverOpen)
                    }
                }
            )

        binding.rvCountriesOpen.adapter = adapterOpenVpn
        binding.rvCountriesOpen.layoutManager = LinearLayoutManager(context)
    }


    private fun goBackMain(serverWire: ServerWire) {
        saveSelectedServerWire(serverWire)
        callBack.showFragment(MainFragment.TAG, false)
    }

    private fun goBackMain(serverOpen: ServerOpenVpn) {
        saveSelectedServerOpenVpn(serverOpen)
        callBack.showFragment(MainFragment.TAG, false)
    }

    private fun saveSelectedServerOpenVpn(serverOpen: ServerOpenVpn) {
        CommonUtils.savePref("type", "OpenVpn")
        CommonUtils.savePref("flagOpen", serverOpen.flagCountry)
        CommonUtils.savePref("nameCityOpen", serverOpen.nameCity)
        CommonUtils.savePref("configOpen", serverOpen.configOpenVpn)
        CommonUtils.savePref("userOpen", serverOpen.userOpenVpn)
        CommonUtils.savePref("passOpen", serverOpen.passwordOpenVpn)
    }

    private fun saveSelectedServerWire(serverWire: ServerWire) {
        CommonUtils.savePref("type", "Wire")
        CommonUtils.savePref("flagCountry", serverWire.flagCountry)
        CommonUtils.savePref("nameCountry", serverWire.nameCity)
        CommonUtils.savePref("privateKeyInterface", serverWire.privateKeyInterface)
        CommonUtils.savePref("addressInterface", serverWire.addressInterface)
        CommonUtils.savePref("dnsInterface", serverWire.dnsInterface)
        CommonUtils.savePref("publicKeyPeer", serverWire.publicKeyPeer)
        CommonUtils.savePref("allowedIPsPeer", serverWire.allowedIPsPeer)
        CommonUtils.savePref("endpointPeer", serverWire.endpointPeer)
    }

    private fun addEvents() {
        binding.ibtBack.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        animationView(v)

        when (v) {
            binding.ibtBack -> goBackMain()
        }
    }

    private fun goBackMain() {
        callBack.showFragment(MainFragment.TAG, false)
    }

    private fun sampleDataWire(): List<CountryWire> {
        return listOf(
            CountryWire(
                "VIET NAM", R.drawable.vn, listOf(
                    ServerWire(
                        "HA NOI",
                        R.drawable.vn,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "Mioou38fh5H+3LWMpitLOWT3JaDGg2gXxqjl2eXkPFU=",
                        "0.0.0.0/0",
                        "vn-hcm.prod.surfshark.com:51820"
                    ),
                    ServerWire(
                        "HO CHI MINH",
                        R.drawable.vn,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "Mioou38fh5H+3LWMpitLOWT3JaDGg2gXxqjl2eXkPFU=",
                        "0.0.0.0/0",
                        "vn-hcm.prod.surfshark.com:51820"
                    ),
                    ServerWire(
                        "HUE",
                        R.drawable.vn,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "Mioou38fh5H+3LWMpitLOWT3JaDGg2gXxqjl2eXkPFU=",
                        "0.0.0.0/0",
                        "vn-hcm.prod.surfshark.com:51820"
                    )
                )
            ),

            CountryWire(
                "SINGAPORE", R.drawable.sg, listOf(
                    ServerWire(
                        "SINGAPORE 1",
                        R.drawable.sg,
                        "mKa/WsKnaqU4UrtHZbIoK65Gu9e6L4/9IC8BuUp3Q2Q=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "MGfgkhJsMVMTO33h1wr76+z6gQr/93VcGdClfbaPsnU=",
                        "0.0.0.0/0",
                        "sg-sng.prod.surfshark.com:51820"
                    ),
                    ServerWire(
                        "SINGAPORE 2",
                        R.drawable.sg,
                        "mKa/WsKnaqU4UrtHZbIoK65Gu9e6L4/9IC8BuUp3Q2Q=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "MGfgkhJsMVMTO33h1wr76+z6gQr/93VcGdClfbaPsnU=",
                        "0.0.0.0/0",
                        "sg-sng.prod.surfshark.com:51820"
                    )
                )
            ),

            CountryWire(
                "MALAYSIA", R.drawable.ml, listOf(
                    ServerWire(
                        "KUALA LUMPUR 1",
                        R.drawable.ml,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "AS84LXlJfgBwGHc70MvtGRxsMqNNucZ2pNOBayJUm04=",
                        "0.0.0.0/0",
                        "my-kul.prod.surfshark.com:51820"
                    ),
                    ServerWire(
                        "KUALA LUMPUR 2",
                        R.drawable.ml,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "AS84LXlJfgBwGHc70MvtGRxsMqNNucZ2pNOBayJUm04=",
                        "0.0.0.0/0",
                        "my-kul.prod.surfshark.com:51820"
                    )
                )
            ),

            CountryWire(
                "UNITED STATE", R.drawable.usa, listOf(
                    ServerWire(
                        "NEW YORK",
                        R.drawable.usa,
                        "aEaEITSWuL44q264194KrROc72xdcJG45f+Rvelt6mc=",
                        "10.66.66.2/32,fd42:42:42::2/128",
                        "1.1.1.1,8.8.8.8",
                        "MZv74zA1ncWaNbWj0+WMYZSQhNd13MwjqzAS6XG2gzE=",
                        "0.0.0.0/0,::/0",
                        "45.63.12.104:52656"
                    ),
                    ServerWire(
                        "CALIFORNIA",
                        R.drawable.usa,
                        "aEaEITSWuL44q264194KrROc72xdcJG45f+Rvelt6mc=",
                        "10.66.66.2/32,fd42:42:42::2/128",
                        "1.1.1.1,8.8.8.8",
                        "MZv74zA1ncWaNbWj0+WMYZSQhNd13MwjqzAS6XG2gzE=",
                        "0.0.0.0/0,::/0",
                        "45.63.12.104:52656"
                    )
                )
            ),

            CountryWire(
                "HONG KONG", R.drawable.mn, listOf(
                    ServerWire(
                        "HONG KONG 1", R.drawable.mn,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "JYHdktdtuM7inbtsxRKSDpnBVTWQ5+QLZ/cWWmf4VRg=",
                        "0.0.0.0/0",
                        "hk-hkg.prod.surfshark.com:51820"
                    ),
                    ServerWire(
                        "HONG KONG 2",
                        R.drawable.usa,
                        "aEaEITSWuL44q264194KrROc72xdcJG45f+Rvelt6mc=",
                        "10.66.66.2/32,fd42:42:42::2/128",
                        "1.1.1.1,8.8.8.8",
                        "MZv74zA1ncWaNbWj0+WMYZSQhNd13MwjqzAS6XG2gzE=",
                        "0.0.0.0/0,::/0",
                        "45.63.12.104:52656"
                    )
                )
            ),

            CountryWire(
                "TAIWAN", R.drawable.tw, listOf(
                    ServerWire(
                        "TAIWAN 1", R.drawable.tw,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "P0vaGUOUE7V5bbGOYY2WgQeZnTZEHvIr+dfebU7W4Ao=",
                        "0.0.0.0/0",
                        "tw-tai.prod.surfshark.com:51820"
                    ),
                    ServerWire(
                        "TAIWAN 2", R.drawable.tw,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "P0vaGUOUE7V5bbGOYY2WgQeZnTZEHvIr+dfebU7W4Ao=",
                        "0.0.0.0/0",
                        "tw-tai.prod.surfshark.com:51820"
                    )
                )
            )
        )
    }

    private fun sampleDataOpenVpn(): List<CountryOpenVpn> {
        return listOf(
            CountryOpenVpn(
                "JAPAN", R.drawable.jp, listOf(
                    ServerOpenVpn(
                        "TOKYO",
                        R.drawable.jp,
                        "ovpn/jp1.ovpn",
                        "test",
                        "nextel",
                    ),
                    ServerOpenVpn(
                        "HIROSHIMA",
                        R.drawable.jp,
                        "ovpn/jp1.ovpn",
                        "test",
                        "nextel",
                    )
                )
            ), CountryOpenVpn(
                "USA", R.drawable.usa, listOf(
                    ServerOpenVpn(
                        "CALIFORNIA",
                        R.drawable.usa,
                        "ovpn/jp1.ovpn",
                        "test",
                        "nextel",
                    ),
                    ServerOpenVpn(
                        "NEWYORK",
                        R.drawable.usa,
                        "ovpn/jp1.ovpn",
                        "test",
                        "nextel",
                    )
                )
            ), CountryOpenVpn(
                "GERMANY", R.drawable.ge, listOf(
                    ServerOpenVpn(
                        "GERMANY 1",
                        R.drawable.ge,
                        "ovpn/jp1.ovpn",
                        "test",
                        "nextel",
                    ),
                    ServerOpenVpn(
                        "GERMANY 2",
                        R.drawable.ge,
                        "ovpn/jp1.ovpn",
                        "test",
                        "nextel",
                    )
                )
            ), CountryOpenVpn(
                "VIETNAM", R.drawable.vn, listOf(
                    ServerOpenVpn(
                        "HOCHIMINH",
                        R.drawable.vn,
                        "ovpn/jp1.ovpn",
                        "test",
                        "nextel",
                    ),
                    ServerOpenVpn(
                        "HANOI",
                        R.drawable.vn,
                        "ovpn/jp1.ovpn",
                        "test",
                        "nextel",
                    )
                )
            )
        )
    }
}