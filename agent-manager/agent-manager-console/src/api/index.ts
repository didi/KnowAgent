/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
function getApi(sys: string, path: string) {
  const prefix = "/api/v1";
  return `${prefix}${sys}${path}`;
}

const api = {
  getData: (id: number) => getApi("", `/${id}`),
};

export default api;
