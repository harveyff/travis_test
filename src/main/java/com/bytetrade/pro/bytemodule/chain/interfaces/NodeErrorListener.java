package com.bytetrade.pro.bytemodule.chain.interfaces;

import com.bytetrade.pro.bytemodule.chain.models.BaseResponse;

/**
 * Interface to be implemented by any listener to network errors.
 */
public interface NodeErrorListener {
    void onError(BaseResponse.Error error);
}
