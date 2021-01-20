package com.CL.slcscanner.Utils;

import com.CL.slcscanner.Pojo.ListResponse.List;
import com.CL.slcscanner.Pojo.PoleMaster.Datum;

public interface MyCallbackForControl {
        //void onClickForControl(int position, Datum objSlcsBean, boolean flag);
        void onClickForControl(int position, List objSlcsBean, boolean flag);
    }