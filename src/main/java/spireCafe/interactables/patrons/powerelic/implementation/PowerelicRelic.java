package spireCafe.interactables.patrons.powerelic.implementation;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import spireCafe.abstracts.AbstractSCCard;
import spireCafe.abstracts.AbstractSCRelic;
import spireCafe.util.Wiz;

import static spireCafe.Anniv7Mod.makeID;

public class PowerelicRelic extends AbstractSCRelic {

    public static final String ID = makeID(PowerelicRelic.class.getSimpleName());

    public AbstractCard capturedCard;
    public PowerelicRelic(AbstractCard card) {
        this();
        setCardInfo(card);
    }
    public PowerelicRelic() {
        super(ID, null, RelicTier.SPECIAL, LandingSound.MAGICAL);
    }



    public void setCardInfo(AbstractCard card){
        if(card!=null && card.type==AbstractCard.CardType.POWER && Wiz.deck().contains(card)){
            capturedCard = card;
            PowerelicCardContainmentFields.isContained.set(card, true);
            PowerelicCardContainmentFields.withinRelic.set(card, this);
            this.tips.clear();
            description=card.rawDescription;
            description=description.replace("!M!",Integer.toString(card.magicNumber));
            if (card instanceof AbstractSCCard) {
                description=description.replace("!M2!",Integer.toString(((AbstractSCCard) card).secondMagic));
            }
            this.outlineImg=img;
            ////we commented this out as it breaks savefiles. maybe we can do something to restore it later?
            //ReflectionHacks.setPrivateInherited(this,PowerelicRelic.class,"relicId",card.name);
            ReflectionHacks.setPrivateInherited(this,PowerelicRelic.class,"name",card.name);

            this.tips.add(new PowerTip(card.name, this.description));

            initializeTips();
        }else{
            //consider panicking here
        }
    }

    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel){
        //do nothing
    }


    @Override
    public void atPreBattle(){
        if(capturedCard!=null){
            capturedCard.use(Wiz.adp(),null);
            flash();
        }
    }




    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class PowerelicCardContainmentFields
    {
        public static SpireField<Boolean> isContained = new SpireField<>(() -> false);
        public static SpireField<AbstractRelic> withinRelic = new SpireField<>(() -> null);
    }

    @Override
    public void renderInTopPanel(SpriteBatch sb) {
        if (this.capturedCard != null) {
            renderCardInstead(sb);
        }else{
            super.render(sb);
        }
    }
    @Override
    public void render(SpriteBatch sb, boolean renderAmount, Color outlineColor) {
        if(this.capturedCard != null){
            renderCardInstead(sb);
        }else{
            super.render(sb, renderAmount, outlineColor);
        }
    }

    private void renderCardInstead(SpriteBatch sb){
        renderPowerCardAtLocation(sb,capturedCard,this.currentX,this.currentY+8*Settings.scale-30f*this.scale,this.scale/Settings.scale);
    }

    private static void renderPowerCardAtLocation(SpriteBatch sb, AbstractCard capturedCard, float x, float y, float drawScale){
        //drawScale parameter does NOT include Settings.scale
        //note -- relic.scale has Settings.scale baked in already! (and card.drawscale does not)
        final float IMAGE_SCALE = 0.25f;
        float tempx=capturedCard.current_x;
        float tempy=capturedCard.current_y;
        float temps=capturedCard.drawScale;
        float tempa=capturedCard.angle;

        capturedCard.current_x=x;
        capturedCard.current_y=y;
        capturedCard.drawScale=drawScale*IMAGE_SCALE;
        capturedCard.angle=0;

        //capturedCard.render(sb);
        if (!UnlockTracker.betaCardPref.getBoolean(capturedCard.cardID, false) && !Settings.PLAYTESTER_ART_MODE) {
            ReflectionHacks.privateMethod(AbstractCard.class, "renderPortrait",SpriteBatch.class).invoke(capturedCard, sb);
        }else{
            ReflectionHacks.privateMethod(AbstractCard.class, "renderJokePortrait",SpriteBatch.class).invoke(capturedCard, sb);
        }
        ReflectionHacks.privateMethod(AbstractCard.class, "renderPortraitFrame",SpriteBatch.class,float.class,float.class).invoke(capturedCard,sb,capturedCard.current_x,capturedCard.current_y);

        capturedCard.current_x=tempx;
        capturedCard.current_y=tempy;
        capturedCard.drawScale=temps;
        capturedCard.angle=tempa;
    }


    @SpirePatch2(clz=SingleRelicViewPopup.class,method="renderRelicImage")
    public static class RelicInspectScreenPatch{
        @SpirePrefixPatch
        public static SpireReturn<Void> patch(SingleRelicViewPopup __instance, SpriteBatch sb){
            AbstractRelic relic=ReflectionHacks.getPrivate(__instance,SingleRelicViewPopup.class,"relic");
            if(relic instanceof PowerelicRelic) {
                PowerelicRelic prRelic=((PowerelicRelic) relic);
                if (prRelic.capturedCard != null){
                    renderPowerCardAtLocation(sb,prRelic.capturedCard, Settings.WIDTH/2f,Settings.HEIGHT/2f+20f*Settings.scale,
                            2.25f);
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }


}
