package info.nightscout.androidaps.plugins.general.automation.triggers

import android.widget.LinearLayout
import com.google.common.base.Optional
import info.nightscout.androidaps.MainApp
import info.nightscout.androidaps.R
import info.nightscout.androidaps.logging.LTag
import info.nightscout.androidaps.plugins.general.automation.elements.Comparator
import info.nightscout.androidaps.plugins.general.automation.elements.InputString
import info.nightscout.androidaps.plugins.general.automation.elements.LabelWithElement
import info.nightscout.androidaps.plugins.general.automation.elements.LayoutBuilder
import info.nightscout.androidaps.plugins.general.automation.elements.StaticLabel
import info.nightscout.androidaps.receivers.NetworkChangeReceiver
import info.nightscout.androidaps.utils.JsonHelper
import org.json.JSONObject

class TriggerWifiSsid(mainApp: MainApp) : Trigger(mainApp) {
    private var ssid = InputString(mainApp)
    var comparator = Comparator(mainApp)

    constructor(mainApp: MainApp, ssid: String, compare: Comparator.Compare) : this(mainApp) {
        this.ssid = InputString(mainApp, ssid)
        comparator = Comparator(mainApp, compare)
    }

    constructor(mainApp: MainApp, triggerWifiSsid: TriggerWifiSsid) : this(mainApp) {
        this.ssid = InputString(mainApp, triggerWifiSsid.ssid.value)
        comparator = Comparator(mainApp, triggerWifiSsid.comparator.value)
    }

    override fun shouldRun(): Boolean {
        val eventNetworkChange = NetworkChangeReceiver.getLastEvent() ?: return false
        if (!eventNetworkChange.wifiConnected && comparator.value == Comparator.Compare.IS_NOT_AVAILABLE) {
            aapsLogger.debug(LTag.AUTOMATION, "Ready for execution: " + friendlyDescription())
            return true
        }
        if (eventNetworkChange.wifiConnected && comparator.value.check(eventNetworkChange.connectedSsid(), ssid.value)) {
            aapsLogger.debug(LTag.AUTOMATION, "Ready for execution: " + friendlyDescription())
            return true
        }
        aapsLogger.debug(LTag.AUTOMATION, "NOT ready for execution: " + friendlyDescription())
        return false
    }

    override fun toJSON(): String {
        val data = JSONObject()
            .put("ssid", ssid.value)
            .put("comparator", comparator.value.toString())
        return JSONObject()
            .put("type", this::class.java.name)
            .put("data", data)
            .toString()
    }

    override fun fromJSON(data: String): Trigger {
        val d = JSONObject(data)
        ssid.value = JsonHelper.safeGetString(d, "ssid")!!
        comparator.value = Comparator.Compare.valueOf(JsonHelper.safeGetString(d, "comparator")!!)
        return this
    }

    override fun friendlyName(): Int = R.string.ns_wifi_ssids

    override fun friendlyDescription(): String =
        resourceHelper.gs(R.string.wifissidcompared, resourceHelper.gs(comparator.value.stringRes), ssid.value)

    override fun icon(): Optional<Int?> = Optional.of(R.drawable.ic_network_wifi)

    override fun duplicate(): Trigger = TriggerWifiSsid(mainApp, this)

    override fun generateDialog(root: LinearLayout) {
        LayoutBuilder()
            .add(StaticLabel(mainApp, R.string.ns_wifi_ssids, this))
            .add(comparator)
            .add(LabelWithElement(mainApp, resourceHelper.gs(R.string.ns_wifi_ssids) + ": ", "", ssid))
            .build(root)
    }
}