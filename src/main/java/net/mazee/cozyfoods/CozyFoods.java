package net.mazee.cozyfoods;

import net.mazee.cozyfoods.block.ModBlocks;
import net.mazee.cozyfoods.block.entity.ModBlockEntities;
import net.mazee.cozyfoods.block.entity.ModEntities;
import net.mazee.cozyfoods.block.entity.client.model.*;
import net.mazee.cozyfoods.block.entity.renderer.*;
import net.mazee.cozyfoods.effect.ModMobEffects;
import net.mazee.cozyfoods.item.ModCreativeModeTab;
import net.mazee.cozyfoods.item.ModItems;
import net.mazee.cozyfoods.item.custom.ModArmorMaterials;
import net.mazee.cozyfoods.recipe.ModRecipes;
import net.mazee.cozyfoods.screen.KettleScreen;
import net.mazee.cozyfoods.screen.ModMenuTypes;
import net.mazee.cozyfoods.screen.SpinnerScreen;
import net.mazee.cozyfoods.world.feature.ModTrunkPlacers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CozyFoods.MODID)
public class CozyFoods {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cozyfoods";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CozyFoods(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);


        ModTrunkPlacers.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModMobEffects.register(modEventBus);

        //ModLootModifiers.register(modEventBus);

        ModCreativeModeTab.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
//            event.accept(ModItems.CASSAVA.get());
//        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());


            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LYCHEE_DOOR.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LYCHEE_TRAPDOOR.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.MANGO_DOOR.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.MANGO_TRAPDOOR.get(), RenderType.cutout());

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SPINNER.get(), RenderType.cutout());

            EntityRenderers.register(ModEntities.CHAIR.get(), ChairRenderer::new);

        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.SPINNER.get(), SpinnerBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.SPINNER_MENU.get(), SpinnerScreen::new);
            event.register(ModMenuTypes.KETTLE_MENU.get(), KettleScreen::new);
        }

        @SubscribeEvent
        public static void registerLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(BeeEarsRenderer.MODEL, BeeEarsModel::createBodyModel);
            event.registerLayerDefinition(PandaEarsRenderer.MODEL, PandaEarsModel::createBodyModel);
            event.registerLayerDefinition(WolfEarsRenderer.MODEL, WolfEarsModel::createBodyModel);
            event.registerLayerDefinition(CatEarsRenderer.MODEL, CatEarsModel::createBodyModel);
            event.registerLayerDefinition(BunnyEarsRenderer.MODEL, BunnyEarsModel::createBodyModel);
        }

        @SubscribeEvent
        public static void addLayers(final EntityRenderersEvent.AddLayers event) {
            event.getSkins().forEach(name -> {
                if(event.getSkin(name) instanceof PlayerRenderer renderer) {
                    renderer.addLayer(new BeeEarsRenderer<>(renderer, event.getEntityModels()));
                    renderer.addLayer(new PandaEarsRenderer<>(renderer, event.getEntityModels()));
                    renderer.addLayer(new WolfEarsRenderer<>(renderer, event.getEntityModels()));
                    renderer.addLayer(new CatEarsRenderer<>(renderer, event.getEntityModels()));
                    renderer.addLayer(new BunnyEarsRenderer<>(renderer, event.getEntityModels()));
                }
            });
            if(event.getRenderer(EntityType.ARMOR_STAND) instanceof ArmorStandRenderer renderer) {
                renderer.addLayer(new BeeEarsRenderer<>(renderer, event.getEntityModels()));
                renderer.addLayer(new PandaEarsRenderer<>(renderer, event.getEntityModels()));
                renderer.addLayer(new WolfEarsRenderer<>(renderer, event.getEntityModels()));
                renderer.addLayer(new CatEarsRenderer<>(renderer, event.getEntityModels()));
                renderer.addLayer(new BunnyEarsRenderer<>(renderer, event.getEntityModels()));
            }

        }
    }
}
