const useEnvironment = () => {
  return {
    getEnvVar: (varName: string) => {
      if (!process.env[varName]) {
        console.error(`No variable found with name ${varName}`);
      }

      return process.env[varName] || "";
    },
  };
};

export default useEnvironment;
