package net.malisis.doors.command;

import net.malisis.doors.block.Door;
import net.malisis.doors.tileentity.DoorTileEntity;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;

/**
 * Command to assign faction requirements to MalisisDoors doors.
 */
public class MalisisDoorsCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "malisisdoors";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/malisisdoors faction <FactionName> <FactionScore>";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 3 || !"faction".equalsIgnoreCase(args[0]))
            throw new WrongUsageException(getUsage(sender));

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        String faction = args[1];
        int score = parseInt(args[2]);

        Vec3d eye = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d reach = eye.addVector(look.x * 5, look.y * 5, look.z * 5);
        RayTraceResult ray = player.world.rayTraceBlocks(eye, reach, false, false, false);
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK)
            throw new CommandException("No door targeted");

        BlockPos pos = ray.getBlockPos();
        DoorTileEntity te = Door.getDoor(player.world, pos);
        if (te == null)
            throw new CommandException("No MalisisDoors door at target");

        te.setFaction(faction, score);
        te.markDirty();
        sender.sendMessage(new TextComponentString("Door faction set to " + faction + " with score " + score));
    }
}
