package com.huynhngoctai.vpn_ict.view.frgment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.adapter.CountriesAdapter
import com.huynhngoctai.vpn_ict.databinding.FragmentSeverListBinding
import com.huynhngoctai.vpn_ict.model.Country
import com.huynhngoctai.vpn_ict.model.Servers
import com.huynhngoctai.vpn_ict.util.CommonUtils

class SeverListFragment : BaseFragment<FragmentSeverListBinding>() {
    companion object {
        val TAG: String = SeverListFragment::class.qualifiedName!!
    }

    private fun sampleData(): List<Country> {
        return listOf(
            Country(
                "VIET NAM", R.drawable.vn, listOf(
                    Servers(
                        "HA NOI",
                        R.drawable.vn,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "Mioou38fh5H+3LWMpitLOWT3JaDGg2gXxqjl2eXkPFU=",
                        "0.0.0.0/0",
                        "vn-hcm.prod.surfshark.com:51820"
                    ),
                    Servers(
                        "HO CHI MINH",
                        R.drawable.vn,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "Mioou38fh5H+3LWMpitLOWT3JaDGg2gXxqjl2eXkPFU=",
                        "0.0.0.0/0",
                        "vn-hcm.prod.surfshark.com:51820"
                    ),
                    Servers(
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

            Country(
                "SINGAPORE", R.drawable.sg, listOf(
                    Servers(
                        "SINGAPORE 1",
                        R.drawable.sg,
                        "mKa/WsKnaqU4UrtHZbIoK65Gu9e6L4/9IC8BuUp3Q2Q=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "MGfgkhJsMVMTO33h1wr76+z6gQr/93VcGdClfbaPsnU=",
                        "0.0.0.0/0",
                        "sg-sng.prod.surfshark.com:51820"
                    ),
                    Servers(
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

            Country(
                "MALAYSIA", R.drawable.ml, listOf(
                    Servers(
                        "KUALA LUMPUR 1",
                        R.drawable.ml,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "AS84LXlJfgBwGHc70MvtGRxsMqNNucZ2pNOBayJUm04=",
                        "0.0.0.0/0",
                        "my-kul.prod.surfshark.com:51820"
                    ),
                    Servers(
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

            Country(
                "UNITED STATE", R.drawable.usa, listOf(
                    Servers(
                        "NEW YORK",
                        R.drawable.usa,
                        "aEaEITSWuL44q264194KrROc72xdcJG45f+Rvelt6mc=",
                        "10.66.66.2/32,fd42:42:42::2/128",
                        "1.1.1.1,8.8.8.8",
                        "MZv74zA1ncWaNbWj0+WMYZSQhNd13MwjqzAS6XG2gzE=",
                        "0.0.0.0/0,::/0",
                        "45.63.12.104:52656"
                    ),
                    Servers(
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

            Country(
                "HONG KONG", R.drawable.mn, listOf(
                    Servers(
                        "HONG KONG 1", R.drawable.mn,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "JYHdktdtuM7inbtsxRKSDpnBVTWQ5+QLZ/cWWmf4VRg=",
                        "0.0.0.0/0",
                        "hk-hkg.prod.surfshark.com:51820"
                    ),
                    Servers(
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

            Country(
                "TAIWAN", R.drawable.tw, listOf(
                    Servers(
                        "TAIWAN 1", R.drawable.tw,
                        "ANk1QsGwNqScFd9jB5lVRLsuBYb65Pu1BcrvVpT4RHw=",
                        "10.14.0.2/16",
                        "162.252.172.57, 149.154.159.92",
                        "P0vaGUOUE7V5bbGOYY2WgQeZnTZEHvIr+dfebU7W4Ao=",
                        "0.0.0.0/0",
                        "tw-tai.prod.surfshark.com:51820"
                    ),
                    Servers(
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

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSeverListBinding {

        return FragmentSeverListBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        addEvents()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val adapter =
            CountriesAdapter(sampleData(), object : CountriesAdapter.OnServerItemClickListener {
                override fun onServerItemClicked(server: Servers) {

                    goBackMain(server)
                }
            }
            )

        binding.rvCountries.adapter = adapter
        binding.rvCountries.layoutManager = LinearLayoutManager(context)
    }


    private fun goBackMain(server: Servers) {
        saveSelectedServer(server)
        callBack.showFragment(MainFragment.TAG, false)
    }

    private fun saveSelectedServer(server: Servers) {
        CommonUtils.savePref("flagCountry", server.flagCountry)
        CommonUtils.savePref("nameCountry", server.nameCity)
        CommonUtils.savePref("privateKeyInterface", server.privateKeyInterface)
        CommonUtils.savePref("addressInterface", server.addressInterface)
        CommonUtils.savePref("dnsInterface", server.dnsInterface)
        CommonUtils.savePref("publicKeyPeer", server.publicKeyPeer)
        CommonUtils.savePref("allowedIPsPeer", server.allowedIPsPeer)
        CommonUtils.savePref("endpointPeer", server.endpointPeer)
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
}