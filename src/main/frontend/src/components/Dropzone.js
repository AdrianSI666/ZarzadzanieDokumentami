/* eslint-disable react/no-unescaped-entities */
import React from "react";
import { useDropzone } from "react-dropzone";

// eslint-disable-next-line react/prop-types
const Dropzone = ({ onDrop }) => {
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop
  });

  return (
    <div className="dropzone-div" {...getRootProps()}>
      <input className="dropzone-input" {...getInputProps()} />
      <div className="text-center">
        {isDragActive ? (
          <p className="dropzone-content">Przeciągnij tutaj by dodać dokument</p>
        ) : (
          <p className="dropzone-content">
            Przytrzymaj i przeciągnij tutaj by dodać dokument lub kliknij by wybrać dokument z listy do dodania
          </p>
        )}
      </div>
    </div>
  );
};

export default Dropzone;