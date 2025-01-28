package spireCafe.interactables.patrons.purpletear;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import spireCafe.abstracts.AbstractCutscene;
import spireCafe.abstracts.AbstractNPC;
import spireCafe.ui.Dialog;
import spireCafe.util.cutsceneStrings.CutsceneStrings;
import spireCafe.util.cutsceneStrings.LocalizedCutsceneStrings;

import java.util.ArrayList;
import java.util.Collections;

import static spireCafe.Anniv7Mod.makeID;

public class PurpleTearCutscene extends AbstractCutscene {
    public static final String ID = makeID(PurpleTearCutscene.class.getSimpleName());
    private static final CutsceneStrings cutsceneStrings = LocalizedCutsceneStrings.getCutsceneStrings(ID);

    AbstractRelic commonBook = new BookOfRCorp();
    BookOfIndex rareBook = new BookOfIndex();
    AbstractCard examplePrescript = rareBook.generateExamplePrescript();

    public PurpleTearCutscene(AbstractNPC character) {
        super(character, cutsceneStrings);
    }

    @Override
    protected void onClick() {
        if (dialogueIndex == 1) {
            nextDialogue();
            this.dialog.addDialogOption(OPTIONS[0]).setOptionResult((i)->{
                nextDialogue();
            });
            this.dialog.addDialogOption(OPTIONS[1]).setOptionResult((i)->{
                goToDialogue(10);
                character.setCutscenePortrait("Portrait3");
            });
        } else if (dialogueIndex == 3) {
            nextDialogue();
            character.setCutscenePortrait("Portrait2");
        } else if (dialogueIndex == 5) {
            nextDialogue();
            character.setCutscenePortrait("Portrait1");
            if (hasRelicOfRarity(AbstractRelic.RelicTier.COMMON)) {
                this.dialog.addDialogOption(OPTIONS[2] + FontHelper.colorString(OPTIONS[3] + OPTIONS[4] + OPTIONS[7], "r") + " " + FontHelper.colorString(OPTIONS[8], "g"), new Clone(), commonBook).setOptionResult((i)->{
                    generateLoseRelicOptions(AbstractRelic.RelicTier.COMMON, commonBook);
                });
            } else {
                this.dialog.addDialogOption(OPTIONS[10], true);
            }

            if (hasRelicOfRarity(AbstractRelic.RelicTier.RARE)) {
                this.dialog.addDialogOption(OPTIONS[2] + FontHelper.colorString(OPTIONS[3] + OPTIONS[6] + OPTIONS[7], "r") + " " + FontHelper.colorString(OPTIONS[8], "g"), examplePrescript, rareBook).setOptionResult((i)->{
                    generateLoseRelicOptions(AbstractRelic.RelicTier.RARE, rareBook);
                });
            }else {
                this.dialog.addDialogOption(OPTIONS[12], true);
            }

            this.dialog.addDialogOption(OPTIONS[9]).setOptionResult((j)->{
                goToDialogue(9);
            });
        } else if (dialogueIndex == 8 || dialogueIndex == 9 || dialogueIndex == 11) {
            endCutscene();
        } else {
            nextDialogue();
        }
    }

    private static boolean hasRelicOfRarity(AbstractRelic.RelicTier tier) {
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.tier == tier) {
                return true;
            }
        }
        return false;
    }

    private void generateLoseRelicOptions(AbstractRelic.RelicTier tier, AbstractRelic reward) {
        character.alreadyPerformedTransaction = true;
        Dialog.optionList.clear();
        ArrayList<AbstractRelic> eligibleRelics = new ArrayList<>();
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.tier == tier) {
                eligibleRelics.add(relic);
            }
        }
        Collections.shuffle(eligibleRelics, AbstractDungeon.miscRng.random);
        int maxOptions = Math.min(3, eligibleRelics.size());
        for (int i = 0; i < maxOptions; i++) {
            AbstractRelic relic = eligibleRelics.get(i);
            this.dialog.addDialogOption(FontHelper.colorString(OPTIONS[13] + relic.name + OPTIONS[14], "r"), relic).setOptionResult((j)->{
                nextDialogue();
                AbstractDungeon.player.loseRelic(relic.relicId);
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), reward);
            });
        }
    }

    public static boolean canSpawn() {
        return hasRelicOfRarity(AbstractRelic.RelicTier.COMMON) || hasRelicOfRarity(AbstractRelic.RelicTier.RARE);
    }

}