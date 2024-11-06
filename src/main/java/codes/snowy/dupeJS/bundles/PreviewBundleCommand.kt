package codes.snowy.dupeJS.bundles

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.entity.Player

@CommandAlias("previewbundle")
@CommandPermission("bundle.preview")
class PreviewBundleCommand : BaseCommand() {

    @Default
    @CommandCompletion("@bundles")
    fun onPreview(sender: Player, @Single bundleName: String) {
        val bundle = BundleManager.getBundle(bundleName) ?: run {
            return
        }

        BundleGUI.openBundlePreview(sender, bundle)
    }
}
