package spireCafe.interactables.npcs.marisa;

import basemod.animations.SpriterAnimation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import spireCafe.Anniv7Mod;
import spireCafe.abstracts.AbstractNPC;
import spireCafe.util.TexLoader;

public class MarisaNPC extends AbstractNPC {
    public static final String ID = MarisaNPC.class.getSimpleName();
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(Anniv7Mod.makeID(ID));

    public MarisaNPC(float animationX, float animationY) {
        super(animationX, animationY, 160.0f, 200.0f);
        this.name = characterStrings.NAMES[0];
        this.animation = new SpriterAnimation(Anniv7Mod.makeCharacterPath("Marisa/Spriter/MarisaAnimation.scml"));
        this.cutscenePortrait = new TextureRegion(TexLoader.getTexture(Anniv7Mod.makeCharacterPath("Marisa/Portrait.png")));
    }

    public void renderCutscenePortrait(SpriteBatch sb) {
        sb.draw(cutscenePortrait, (1460.0F - (cutscenePortrait.getRegionWidth() / 2.0F)) * Settings.scale, 0 * Settings.scale, 0.0F, 0.0F, cutscenePortrait.getRegionWidth(), cutscenePortrait.getRegionHeight(), Settings.scale, Settings.scale, 0.0F);
    }

    public void onInteract() {
        AbstractDungeon.topLevelEffectsQueue.add(new MarisaCutscene(this));
    }
}
