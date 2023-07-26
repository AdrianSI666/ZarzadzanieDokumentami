import { configureStore, combineReducers } from "@reduxjs/toolkit";
import configReducer from "./config";
import storage from "redux-persist/lib/storage";
import {persistReducer,
  REHYDRATE,
  PERSIST} from "redux-persist";

const persistConfig = {
  key: "root",
  storage
}

const reducer = combineReducers({
  config: configReducer,
})

const persistedReducer = persistReducer(persistConfig, reducer)

export default configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [REHYDRATE, PERSIST],
      },
    }),
});