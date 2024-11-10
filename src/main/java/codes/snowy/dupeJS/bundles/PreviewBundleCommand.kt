package codes.snowy.dupeJS.bundles

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("previewbundle")
@CommandPermission("bundle.preview")
class PreviewBundleCommand : BaseCommand() {

    @HelpCommand
    @Syntax("[query]")
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Default
    @CommandCompletion("@bundles")
    fun onPreview(sender: Player, @Single bundleName: String) {
        val bundle = BundleManager.getBundle(bundleName) ?: run {
            return
        }

        BundleGUI.openBundlePreview(sender, bundle)
    }
}
