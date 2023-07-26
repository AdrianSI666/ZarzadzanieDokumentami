import { createSlice } from "@reduxjs/toolkit";

export const configSlice = createSlice({
  name: "config",
  initialState: {
    localHost: "localhost",
    port: "8091",
    loggedIn: false,
    user: null,
    isMannager: false
  },
  reducers: {
    loggInUser: (state, action) => {
      state.loggedIn = true
      state.user = action.payload
    },
    loggOfUser: (state) => {
      state.loggedIn = false
      state.user = null
    },
    loggInMannager: (state, action) => {
      state.isMannager = action.payload
    }
  }
});

// Action creators are generated for each case reducer function
export const { loggInUser, loggOfUser, loggInMannager } = configSlice.actions;

export default configSlice.reducer;
