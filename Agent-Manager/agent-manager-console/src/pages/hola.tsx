import Hello from "../container/Hola";
import React from "react";
import { initialState } from "../reducers/hola";

export const HOLA = () => {
  const props = initialState as any;
  return (
    <Hello {...props}/>
  );
};
