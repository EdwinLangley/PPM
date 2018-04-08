// RTaskManager.cs
// /Users/darylcecile/Desktop/PPM.nosync/RiverCSharp/Riviera/Riviera
// Daryl Cecile Copyright 2018
// 08/04/2018

using System;
using System.Linq;
using System.Collections.Generic;
using RivieraWeb;

namespace RivieraManagers
{
    public static class RTaskManager
    {
        public static List<RTask> taskList = new List<RTask>();

        public struct RTask{
            public string identifier;
            public Action handler;

            public void Complete(){
                if (handler != null) handler();
                RWebClient.TriggerCallback(identifier);
                taskList.Remove(this);
            }
        }

        public static string GenID(int length = 8){
            Random random = new Random();
            const string pool = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            var chars = Enumerable.Range(0, length)
                .Select(x => pool[random.Next(0, pool.Length)]);
            return new string(chars.ToArray());
        }

        public static RTask CreateTask(Action handler=null){
            RTask rTask = new RTask
            {
                identifier = GenID(),
                handler = handler
            };
            taskList.Add(rTask);
            return rTask;
        }

    }
}
