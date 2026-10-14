package com.ghost.client.gui;

import com.ghost.client.GhostClient;
import com.ghost.client.module.Module;
import com.ghost.client.module.config.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.List;

public class GhostScreen extends Screen {
    private static final int PX=10,PY=10,PW=130,MH=14,HH=16,SH=12,CX=150,CW=160;
    private Module selectedModule = null;
    private Setting<?> dragging = null;

    public GhostScreen() { super(Text.literal("Ghost")); }

    @Override public boolean shouldPause() { return false; }
    @Override public void close() { super.close(); GhostClient.getClickGUI().setOpen(false); }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        ctx.fill(0,0,width,height,0x88000000);
        List<Module> mods = GhostClient.getModuleManager().getModules();
        int ph = HH + mods.size()*MH + 4;
        panel(ctx,PX,PY,PW,ph);
        ctx.fill(PX,PY,PX+PW,PY+HH,0xFF1a1a2e);
        ctx.drawCenteredTextWithShadow(textRenderer,"§b§lGHOST",PX+PW/2,PY+4,0xFFFFFF);
        int ry=PY+HH+2;
        for (Module m:mods) {
            boolean hov=mx>=PX&&mx<=PX+PW&&my>=ry&&my<ry+MH;
            ctx.fill(PX+2,ry,PX+PW-2,ry+MH-1,m.isEnabled()?0xFF16213e:hov?0xFF2a2a3e:0xFF1a1a2e);
            ctx.fill(PX+5,ry+4,PX+10,ry+9,m.isEnabled()?0xFF00e5ff:0xFF555555);
            ctx.drawTextWithShadow(textRenderer,Text.literal(m.getName()),PX+14,ry+3,m.isEnabled()?0xFFFFFFFF:0xFF888888);
            ry+=MH;
        }
        if (selectedModule!=null && !selectedModule.getSettings().isEmpty()) {
            int sy=PY,sh=HH+selectedModule.getSettings().size()*SH+8;
            panel(ctx,CX,sy,CW,sh);
            ctx.fill(CX,sy,CX+CW,sy+HH,0xFF0f3460);
            ctx.drawCenteredTextWithShadow(textRenderer,"§e"+selectedModule.getName(),CX+CW/2,sy+4,0xFFFFFF);
            int sry=sy+HH+4;
            for (Setting<?> s:selectedModule.getSettings()) {
                ctx.drawTextWithShadow(textRenderer,Text.literal("§7"+s.getName()+": §f"+fmt(s)),CX+6,sry+1,0xFFFFFF);
                if (s.getValue() instanceof Number && s.getMin()!=null) {
                    double v=((Number)s.getValue()).doubleValue(),lo=((Number)s.getMin()).doubleValue(),hi=((Number)s.getMax()).doubleValue();
                    int bw=CW-12,filled=(int)((v-lo)/(hi-lo)*bw);
                    ctx.fill(CX+6,sry+9,CX+6+bw,sry+11,0xFF333355);
                    ctx.fill(CX+6,sry+9,CX+6+filled,sry+11,0xFF00e5ff);
                }
                sry+=SH;
            }
        }
        super.render(ctx,mx,my,delta);
    }

    @Override
    public boolean mouseClicked(double mx,double my,int btn) {
        List<Module> mods=GhostClient.getModuleManager().getModules();
        int ph=HH+mods.size()*MH+4;
        if (mx>=PX&&mx<=PX+PW&&my>=PY+HH&&my<=PY+ph) {
            int idx=(int)(my-PY-HH-2)/MH;
            if (idx>=0&&idx<mods.size()) {
                Module m=mods.get(idx);
                if (btn==0){m.toggle();GhostClient.getConfigManager().save();}
                else if(btn==1) selectedModule=(selectedModule==m)?null:m;
                return true;
            }
        }
        if (selectedModule!=null) {
            int sry=PY+HH+4;
            for (Setting<?> s:selectedModule.getSettings()) {
                if (s.getValue() instanceof Boolean&&btn==0&&my>=sry&&my<sry+SH&&mx>=CX+6&&mx<=CX+CW-6) {
                    s.setValueRaw(!(Boolean)s.getValue());
                    GhostClient.getConfigManager().save(); return true;
                }
                sry+=SH;
            }
        }
        return super.mouseClicked(mx,my,btn);
    }

    @Override
    public boolean mouseDragged(double mx,double my,int btn,double dx,double dy) {
        if (dragging!=null&&dragging.getMin()!=null) {
            double lo=((Number)dragging.getMin()).doubleValue(),hi=((Number)dragging.getMax()).doubleValue();
            double pct=Math.max(0,Math.min(1,(mx-(CX+6))/(CW-12)));
            double nv=lo+pct*(hi-lo);
            if (dragging.getValue() instanceof Integer) dragging.setValueRaw((int)Math.round(nv));
            else dragging.setValueRaw(Math.round(nv*100.0)/100.0);
            return true;
        }
        return super.mouseDragged(mx,my,btn,dx,dy);
    }

    @Override
    public boolean mouseReleased(double mx,double my,int btn) {
        if (dragging!=null){GhostClient.getConfigManager().save();dragging=null;}
        return super.mouseReleased(mx,my,btn);
    }

    private void panel(DrawContext ctx,int x,int y,int w,int h) {
        ctx.fill(x+3,y+3,x+w+3,y+h+3,0x55000000);
        ctx.fill(x-1,y-1,x+w+1,y+h+1,0xFF00e5ff);
        ctx.fill(x,y,x+w,y+h,0xEE0d0d1a);
    }

    private String fmt(Setting<?> s) {
        Object v=s.getValue();
        if(v instanceof Double) return String.format("%.2f",v);
        if(v instanceof Float)  return String.format("%.2f",v);
        return v.toString();
    }
            }
